import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


internal class FlowPracticalExample {
    private val storeIds = listOf(
        StoreId("00988"),
        StoreId("00966"),
        StoreId("16622"),
        StoreId("12345"))

    @OptIn(ExperimentalTime::class)
    @Test
    fun flowDemoAobToOsp() = runBlocking {
        val duration = measureTime {
            val storeCiFlow = flow {
                storeIds.forEach { storeId ->
                    println("Now fetching CI's for store $storeId")
                    repeat(15) {
                        val randomCi = ConsumerItem(Random.nextInt(111111, 999999).toString())
                        emit(StoreCi(storeId, randomCi))
                    }
                    delay(1500)
                    println("Fetched all CI's for store $storeId")
                }
            }

            storeCiFlow.buffer(25).collect {
                println("Sending override for store ${it.storeId} and ci ${it.ci}")
                delay(100)
            }
        }

        println("All done, everything took $duration")
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun flowDemoAobToOspWithTimeout() = runBlocking {
        val duration = measureTime {
            val storeCiFlow = flow {
                storeIds.forEach { storeId ->
                    println("Now fetching CI's for store $storeId")
                    val storeCis = withTimeoutOrNull(1500) { fetchCisForStore(storeId) }
                    storeCis?.forEach {
                        emit(it)
                    }

                    println("Fetched all CI's for store $storeId")
                }
            }

            storeCiFlow.buffer(25).collect {
                println("Sending override for store ${it.storeId} and ci ${it.ci}")
                delay(100)
            }
        }

        println("All done, everything took $duration")
    }

    private suspend fun fetchCisForStore(storeId: StoreId): List<StoreCi> {
        val storeCis = (1..3).map {
            val randomCi = ConsumerItem(Random.nextInt(111111, 999999).toString())
            StoreCi(storeId, randomCi)
        }

        // Ooops fetching cis for this store took too long
        if (storeId.value == "12345") {
            delay(3000)
        } else {
            delay(1000)
        }

        return storeCis
    }
}

@JvmInline
value class StoreId(val value: String)

@JvmInline
value class ConsumerItem(val value: String)

data class StoreCi(val storeId: StoreId, val ci: ConsumerItem)