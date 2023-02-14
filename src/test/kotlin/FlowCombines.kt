import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class FlowCombines {
    @Test
    fun `test combine`() = runBlocking {
        val slowFlow = (1..3).asFlow().onEach { delay(400) }
        val fastFlow = listOf("A", "B", "C", "D", "E").asFlow().onEach { delay(120) }

        // Values are collected as soon as each flow have a "more recent" emit
        slowFlow.combine(fastFlow) { val1, val2 ->
            "Combined ($val1,$val2)"
        }.collect {
            println(it)
        }
    }

    @Test
    fun `test zip`() = runBlocking {
        val slowFlow = (1..3).asFlow().onEach { delay(400) }
        val fastFlow = listOf("A", "B", "C", "D", "E").asFlow().onEach { delay(120) }

        // Values are collected as soon as each flow have emitted
        slowFlow.zip(fastFlow) { val1, val2 ->
            "Zipped ($val1,$val2)"
        }.collect {
            println(it)
        }
    }

    private fun flowFrom(myFlow: Flow<*>, valueFromOtherFlow: Any) = myFlow.map { "(${it}_$valueFromOtherFlow)" }

    @Test
    @OptIn(FlowPreview::class)
    fun `test flatmap merge`() = runBlocking {
        val slowFlow = (1..3).asFlow().onEach { delay(400) }
        val fastFlow = listOf("A", "B", "C", "D", "E").asFlow().onEach { delay(120) }
        // Values are combined for each flow emit
        slowFlow.flatMapMerge { slowFlowValue -> flowFrom(fastFlow, slowFlowValue) }.collect {
            println("FlatMapMerge: $it")
        }
    }

    @Test
    @OptIn(FlowPreview::class)
    fun `test flatmap combine`() = runBlocking {
        val slowFlow = (1..3).asFlow().onEach { delay(400) }
        val fastFlow = listOf("A", "B", "C", "D", "E").asFlow().onEach { delay(120) }
        // Values are combined sequentially and will start when the first flow is done
        slowFlow.flatMapConcat { slowFlowValue -> flowFrom(fastFlow, slowFlowValue) }.collect {
            println("FlatMapConcat: $it")
        }
    }

    @OptIn(FlowPreview::class)
    @Test
    fun `test multiple combine`() = runBlocking {
        val slowFlow = (1..3).asFlow().onEach { delay(400) }
        val fastFlow = listOf("A", "B", "C").asFlow().onEach { delay(120) }
        val thirdFlow = listOf("X", "Y", "Z").asFlow().onEach { delay(250) }

        // Values are combined sequentially and will start when the first flow is done
        slowFlow
            .flatMapConcat { slowFlowValue -> flowFrom(fastFlow, slowFlowValue) }
            .flatMapConcat { slowAndFastFlowValue -> flowFrom(thirdFlow, slowAndFastFlowValue) }
            .collect {
                println("Multiconcat ($it)")
            }
    }
}