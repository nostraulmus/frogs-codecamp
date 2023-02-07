import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.Test
import kotlin.random.Random

class FlowPracticalExample2 {
    private val storeIds = listOf(
        StoreId("00988"),
        StoreId("00966"),
        StoreId("16622"),
        StoreId("99999")
    )

    @Test
    fun flowDemoAobToOspWithTimeout() = runBlocking {
        val storeCiFlow = flow {
            storeIds.forEach { storeId ->
                println("Now fetching CI's for store ${storeId.value}")
                val storeCis = withTimeoutOrNull(1500) { fetchCisForStore(storeId) }
                storeCis?.forEach {
                    emit(it)
                }

                if (storeCis == null) {
                    println("Store took too long, no overrides sent for store ${storeId.value}")
                } else {
                    println("Fetched all CI's for store ${storeId.value}")
                }
            }
        }

        storeCiFlow.collect {
            println("Sending override for store ${it.storeId.value} and ci ${it.ci.value}")
            delay(100)
        }

        println("All done!")
    }

    private suspend fun fetchCisForStore(storeId: StoreId): List<StoreCi> {
        val storeCis = (1..3).map {
            val randomCi = ConsumerItem(Random.nextInt(1111111, 9999999).toString())
            StoreCi(storeId, randomCi)
        }

        // Ooops fetching cis for this store took too long
        if (storeId.value == "99999") {
            delay(3000)
        } else {
            delay(1000)
        }

        return storeCis
    }

}