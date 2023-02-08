import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
internal class SuspendingTest {
    private suspend fun runOnce(executionNumber: Int): Int {
        delay(1000)
        return executionNumber
    }

    @Test
    fun test_blocking() = runBlocking {
        val duration = measureTime {
            val result = IntRange(1, 10).map { runOnce(it) }
            println("Result is: $result")
        }
        println("These executions took: $duration")
    }

    @Test
    fun test_launch() = runBlocking {
        val duration = measureTime {
            val result = IntRange(1, 10).map { launch { runOnce(it) } }
            println("Result is: ${result.joinAll()}")
        }
        println("These executions took: $duration")
    }

    @Test
    fun test_async() = runBlocking {
        val duration = measureTime {
            val result = IntRange(1, 10).map { async { runOnce(it) } }
            println("Result is: ${result.awaitAll()}")
        }
        println("These executions took: $duration")
    }
}