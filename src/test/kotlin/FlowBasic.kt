import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class FlowBasic {
    @Test
    fun flowDemo() = runBlocking {
        val myFlow = flow {
            repeat(10) {
                emit(it)
                delay(1000)
            }
        }

        myFlow.collect {
            println("Tick tock #$it")
        }
    }

    @Test
    fun `flows with different operators`() = runBlocking {
        val myFlow = flow {
            repeat(10) {
                emit(it)
                delay(1000)
            }
        }

        myFlow
            .map { it + 25 }
            .filter { it % 2 == 0 }
            .transform {
                emit("Tick tock #$it")
                if (it > 33 ) {
                    emit("BOOM!")
                }
            }
            .collect {
                println(it)
            }
    }
}