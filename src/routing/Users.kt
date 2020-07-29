package com.exxbrain.routing

import com.exxbrain.Assert
import com.exxbrain.data.DataAccess
import io.ktor.application.call
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.receive
import io.ktor.request.uri
import io.ktor.response.respond
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

fun convert(user: com.exxbrain.data.User) : User = User(
    username = user.username,
    firstName = user.firstName,
    lastName = user.lastName,
    email = user.email,
    phone = "+${user.phone}"
)

fun Routing.users(dataAccess: DataAccess) {
    post("/users") {
        val user = call.receive<User>()
        assertValid(user)
        val userData = convert(user)
        dataAccess.users.save(userData)
        val url = "${call.request.uri}/${userData.id}"
        call.response.headers.append(HttpHeaders.Location, url)
        call.respond(HttpStatusCode.Created)
    }
    get("/users/{id}") {
        val id = call.parameters["id"]
        val user = dataAccess.users.findById(UUID.fromString(id))
        call.respond(convert(user))
    }
    put("/users/{id}") {
        val user = call.receive<User>()
        assertValid(user)
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

fun assertValid(user: User) {
    Assert.isNotBlank(user.username)
    Assert.lengthIsLessThan(user.username, 256)
    Assert.isNotBlank(user.firstName)
    Assert.isNotBlank(user.lastName)
    Assert.isEmail(user.email)
    Assert.isPhone(user.phone)
}
