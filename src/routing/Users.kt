package com.exxbrain.routing

import com.exxbrain.data.DataAccess
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.receive
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.*
import java.util.*

data class User(
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String
)

fun convert(user: User) : com.exxbrain.data.User = com.exxbrain.data.User(
    username = user.username,
    firstName = user.firstName,
    lastName = user.lastName,
    email = user.email,
    phone = user.phone
)

fun Routing.users(dataAccess: DataAccess) {
    post("/users") {
        val user = call.receive<User>()
        val userData = convert(user)
        dataAccess.users.save(userData)
        call.respondRedirect(url = "${call.request.uri}/${userData.id}")
    }
    get("/users/{id}") {
        val id = call.parameters["id"]
        call.respond(dataAccess.users.findById(UUID.fromString(id)))
    }
    put("/users/{id}") {
        val user = call.receive<User>()
        val userData = convert(user)
        userData.id = UUID.fromString(call.parameters["id"])
        dataAccess.users.save(userData)
        call.respond(OK)
    }
    delete("/users/{id}") {
        val uuid = UUID.fromString(call.parameters["id"])
        dataAccess.users.deleteById(uuid)
        call.respond(NoContent)
    }
}