package com.exxbrain.routing

import io.ktor.application.Application
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get

enum class ServerStatus {OK}
data class Health(val status: ServerStatus)
data class Version(val version: String)

fun Routing.main() {
    get("/") {
        call.respondText("${application.greeting} World!", ContentType.Text.Plain, HttpStatusCode.OK)
    }
    get("/health") {
        call.respond(Health(status = ServerStatus.OK))
    }
    get("/version") {
        call.respond(HttpStatusCode.OK, Version(version = application.version))
    }
}

val Application.version get() = environment.config.property("ktor.application.version").getString()
val Application.greeting get() = environment.config.property("ktor.application.greeting").getString()