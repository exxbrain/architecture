package com.exxbrain

import com.exxbrain.data.DataAccess
import com.exxbrain.database.DatabaseAccess
import com.exxbrain.routing.main
import com.exxbrain.routing.users
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.routing.*
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.slf4j.LoggerFactory
import java.time.Duration

val log = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.main(dataAccess: DataAccess = DatabaseAccess(databaseUri, "org.postgresql.Driver")) {
    install(DefaultHeaders)
    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respond(TextContent("${it.value} ${it.description}",
                ContentType.Text.Plain.withCharset(Charsets.UTF_8), it))
        }
        exception<AssertionError> { cause ->
            call.respond(HttpStatusCode.BadRequest,
                "${HttpStatusCode.BadRequest.value} ${HttpStatusCode.BadRequest.description}")
            log.info(cause.localizedMessage)
        }
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError,
                "${HttpStatusCode.InternalServerError.value} ${HttpStatusCode.InternalServerError.description}")
            throw cause
        }
    }
    install(CallLogging)
    install(CORS) {
        anyHost()
        allowCredentials = true
        listOf(HttpMethod("PATCH"), HttpMethod.Put, HttpMethod.Delete).forEach {
            method(it)
        }
    }
    install(ContentNegotiation) {
        register(ContentType.Application.Json, GsonConverter())
        gson {
            setPrettyPrinting()
        }
    }

    install(MicrometerMetrics) {
        registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

        distributionStatisticConfig = DistributionStatisticConfig.Builder()
                .percentilesHistogram(true)
                .percentiles(0.5, 0.95, 0.99, 1.0)
                .maximumExpectedValue(Duration.ofSeconds(20).toNanos())
                .sla(
                        Duration.ofMillis(100).toNanos(),
                        Duration.ofMillis(500).toNanos()
                )
                .build()

    }

    routing {
        main()
        users(dataAccess)
    }
}

val Application.databaseUri get() = environment.config.property("ktor.application.databaseUri").getString()