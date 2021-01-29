import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.fetch.Response
import kotlin.coroutines.*
import kotlin.js.Promise

suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
    then({ cont.resume(it) }, { cont.resumeWithException(it) })
}

object CS : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext get() = job
}

@Serializable
data class Product(
    val id: String,
    val type: String,
    val name: String,
    val color: Array<String>,
    val price: Int,
    val manufacturer: String
)

val rootUrl = "https://bad-api-assignment.reaktor.com"

suspend fun fetchAndCheck(url: String): Response {
    val response: Response = window.fetch(url).await()
    if (!response.ok) {
        throw Exception("Failed to fetch stuff from ${url}: ${response.status} ${response.statusText}")
    }
    return response
}

suspend inline fun <reified T> fetchJson(url: String): T {
    val json = fetchAndCheck(url).text().await()
    return Json.decodeFromString(json)
}

fun main() {
    document.write("Hello, world!")
    CS.launch {
        val products = async {
            fetchJson<Array<Product>>("${rootUrl}/products/accessories")
        }
        println(products.await())
    }
}
