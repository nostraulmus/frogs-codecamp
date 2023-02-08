import kotlinx.coroutines.*
import org.junit.jupiter.api.Test

internal class Cooperative {
    @Test
    fun `bad bad thread sleep`(): Unit = runBlocking {
        val job = launch {
            repeat(20) {
                print("${it + 1}...")
                Thread.sleep(100)
            }
        }

        launch {
            delay(1000)
            job.cancel(CancellationException("Go away"))
        }
    }

    @Test
    fun `good good delay`() : Unit = runBlocking {
        val job = launch {
            repeat(20) {
                print("${it + 1}...")
                delay(100)
            }
        }

        launch {
            delay(1000)
            job.cancel(CancellationException("Go away"))
        }
    }
}