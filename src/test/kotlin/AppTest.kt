import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.expect

class AppTest {
    @Test
    fun toJson() {
        val product = Product(
            "5",
            "jacket",
            "Jacket",
            listOf("black", "navyblue"),
            50,
            "ACME"
        )
        expect("""{"id":"5","type":"jacket","name":"Jacket","color":["black","navyblue"],"price":50,"manufacturer":"ACME"}""") { Json.encodeToString(product) }
    }

    @Test
    fun fromJson() {
        val json = """{"id":"5","type":"jacket","name":"Jacket","color":["black","navyblue"],"price":50,"manufacturer":"ACME"}"""
        val product = Json.decodeFromString<Product>(json)
        expect(Product(
            "5",
            "jacket",
            "Jacket",
            listOf("black", "navyblue"),
            50,
            "ACME"
        )) { product }
    }
}
