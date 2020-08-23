package com.exxbrain.routing

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

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