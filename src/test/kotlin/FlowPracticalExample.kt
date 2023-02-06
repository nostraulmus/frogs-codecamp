import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


internal class FlowPracticalExample {
    private val storeIds = listOf(StoreId("00988"), StoreId("00966"), StoreId("16622"))

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
}

@JvmInline
value class StoreId(val value: String)

@JvmInline
value class ConsumerItem(val value: String)

data class StoreCi(val storeId: StoreId, val ci: ConsumerItem)