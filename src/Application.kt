package com.exxbrain

import com.exxbrain.database.DatabaseAccess
import com.exxbrain.data.DataAccess
import com.exxbrain.routing.main
import com.exxbrain.routing.users
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.TextContent
import io.ktor.features.*
import io.ktor.gson.GsonConverter
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.withCharset
import io.ktor.response.respond
import io.ktor.routing.routing
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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
            if (cause.message != null && cause.message!!.contains("duplicate")) {
                call.respond(HttpStatusCode.BadRequest,
                        "${HttpStatusCode.BadRequest.value} ${HttpStatusCode.BadRequest.description}")
                log.info(cause.localizedMessage)
                return@exception
            }
            call.respond(HttpStatusCode.InternalServerError,
                "${HttpStatusCode.InternalServerError.value} ${HttpStatusCode.InternalServerError.description}")
            throw cause
        }
    }
    install(CallLogging)
    install(ConditionalHeaders)
    install(AutoHeadResponse)
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

    routing {
        main()
        users(dataAccess)
    }
}

val Application.databaseUri get() = environment.config.property("ktor.application.databaseUri").getString()