import com.exxbrain.database.DatabaseAccess
import com.exxbrain.database.UserTable
import com.exxbrain.main
import com.exxbrain.routing.User
import com.google.gson.Gson
import io.ktor.config.MapApplicationConfig
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.UnstableDefault
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.test.*

@UnstableDefault
class UsersTest {
    @Test
    fun testCrud() = withTestApplication({
        (environment.config as MapApplicationConfig).apply {
            // Set here the properties
            put("ktor.application.path", "/")
        }
        main(DatabaseAccess("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1", "org.h2.Driver"))
    }) {
        transaction {
            SchemaUtils.create(UserTable)
        }
        val user = User(null, "test", "test", "test", "test@test.ru", "+79891233443")
        var userId = ""
        with(handleRequest(HttpMethod.Post, "/users") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(Gson().toJson(user))
        }) {
            val location = response.headers["Location"]
            assertNotNull(location)
            userId = location.split("/").last()
            assertEquals(HttpStatusCode.Created, response.status())
        }

        with(handleRequest(HttpMethod.Post, "/users") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(Gson().toJson(user))
        }) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }

        val updatedUser = User(UUID.fromString(userId), "test1", "test1", "test1", "test1@test1.ru", "+79891233443")
        with(handleRequest(HttpMethod.Put, "/users/$userId") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(Gson().toJson(updatedUser))
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        val invalidUser = User(UUID.fromString(userId), "test1", "test1", "test1", "test1est1.ru", "9891233443")
        with(handleRequest(HttpMethod.Put, "/users/$userId") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(Gson().toJson(invalidUser))
        }) {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }

        with(handleRequest(HttpMethod.Get, "/users/$userId")) {
            val actual = Gson().fromJson(response.content!!, User::class.java)
            assertEquals(updatedUser, actual)
            assertEquals(HttpStatusCode.OK, response.status())
        }

        with(handleRequest(HttpMethod.Delete, "/users/$userId")) {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }
    }
}