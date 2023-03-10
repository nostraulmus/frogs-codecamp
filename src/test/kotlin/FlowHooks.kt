import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class FlowHooks {
    @Test
    fun `flow common hooks`() = runBlocking {
        val data = flow { setOf("A", "B", "C").forEach { emit(it) } }
            .onEach {
                println(it)
                delay(1000L)
            }
            .onStart { println("Flow starting!") }
            .onCompletion { println("Flow ended!") }

        data.collect()
    }
}