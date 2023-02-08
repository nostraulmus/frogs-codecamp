import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class FlowBasic {
    @Test
    fun `basic kotlin flow`() = runBlocking {
        val myFlow = flow {
            repeat(5) {
                emit(it + 1)
                delay(1000)
            }
        }

        myFlow.collect {
            println(it)
        }
    }
}