import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.get
import org.w3c.dom.parsing.DOMParser
import org.w3c.fetch.Response
import kotlin.coroutines.*
import kotlin.js.Promise
import kotlin.reflect.cast
import kotlin.reflect.safeCast

object CS : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext get() = job
}

@Serializable
data class Product(
    val id: String,
    val type: String,
    val name: String,
    val color: List<String>,
    val price: Int,
    val manufacturer: String
)

val rootUrl = "https://bad-api-assignment.reaktor.com"

suspend fun fetchAndCheck(url: String): Response {
    val response: Response = window.fetch(url).await()
    if (!response.ok) {
        throw Exception("Failed to fetch stuff from $url: ${response.status} ${response.statusText}")
    }
    return response
}

suspend inline fun <reified T> fetchJson(url: String): T {
    val json = fetchAndCheck(url).text().await()
    return Json.decodeFromString(json)
}

suspend fun fetchProducts(type: String): List<Product> =
    fetchJson("$rootUrl/products/$type")

/**
 * @property id References [Product.id]
 * @property availability "INSTOCK" | "LESSTHAN10" | "OUTOFSTOCK"
 */
data class Availability(val id: String, val availability: String)

@Serializable
data class AvailabilityRaw(val id: String, val DATAPAYLOAD: String)
@Serializable
data class AvailabilitiesRaw(val code: Int, val response: List<AvailabilityRaw>)

suspend fun fetchAvailability(manufacturer: String): List<Availability> {
    val data: AvailabilitiesRaw = fetchJson("$rootUrl/availability/$manufacturer")
    require(data.code == 200) { "Failed to fetch $manufacturer: ${data.code}" }

    return data.response.map {
        // DATAPAYLOAD is a xml snippet such as "<AVAILABILITY>\n  <INSTOCKVALUE>INSTOCK</INSTOCKVALUE>\n</AVAILABILITY>"
        val xml: Document = DOMParser().parseFromString(it.DATAPAYLOAD, "text/xml")
        val availability: String = xml.getElementsByTagName("INSTOCKVALUE")[0]!!.textContent!!.trim()
        Availability(it.id.toLowerCase(), availability)
    }
}

/**
 * @return map mapping Product.id to Availability for that product.
 */
suspend fun fetchAvailabilities(manufacturers: Set<String>): Map<String, Availability> = coroutineScope {
    val availabilities: List<Availability> = manufacturers
        .map { async { fetchAvailability(it) } }
        .awaitAll()
        .flatten()
    availabilities.associateBy { it.id }
}

fun toDomTableRow(product: Product, availability: Availability?): String =
    """<tr>
    <td>${product.name}</td>
    <td>${product.type}</td>
    <td>${product.manufacturer}</td>
    <td>${product.price}</td>
    <td>${availability?.availability ?: "UNKNOWN"}</td>
    </tr>"""

fun main() {
    CS.launch {
        val products: List<Product> = listOf(
            async { fetchProducts("accessories") },
            async { fetchProducts("jackets") },
            async { fetchProducts("shirts") })
            .awaitAll()
            .flatten()
        console.log("Fetched ${products.size} products")
        val manufacturers: Set<String> = products.map { it.manufacturer } .toSet()
        console.log("Fetching availabilities for $manufacturers")
        val availabilities: Map<String, Availability> = fetchAvailabilities(manufacturers)
        console.log("Fetched availabilities for ${availabilities.size} products");

        val tbody: Element = document.querySelector("tbody")!!
        tbody.innerHTML = products.joinToString("") { p ->
            toDomTableRow(p, availabilities[p.id])
        }
    }
}
