import com.exxbrain.main
import io.ktor.config.MapApplicationConfig
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.UnstableDefault
import kotlin.test.Test
import kotlin.test.assertEquals

@UnstableDefault
class MainTest {
    @Test
    fun testHealthAndVersion() = withTestApplication({
        (environment.config as MapApplicationConfig).apply {
            // Set here the properties
            put("ktor.application.version", "0.0.1")
        }
        main()
    }){
        with (handleRequest(HttpMethod.Get, "/health")) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
        with (handleRequest(HttpMethod.Get, "/version")) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }
}