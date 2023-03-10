import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class FlowCombines {
    @Test
    fun `flow dependencies with collect in flow builder`() = runBlocking {
        val parentFlow = flow { setOf("A", "B", "C").forEach { emit(it) } }
            .onEach {
                println(it)
                delay(200L)
            }

        val childFlow = flow {
            parentFlow.collect()
            setOf("1", "2", "3").forEach { emit(it) }
        }
            .onEach {
                println(it)
                delay(200L)
            }

        childFlow.collect()
    }

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

    @Test
    fun `combine sets`() = runBlocking {
        val empty = flowOf(emptySet<String>())
        val set1 = flowOf(setOf("A", "B", "C"))
        val set2 = flowOf(setOf("C", "D", "E"))
        val set3 = flowOf(setOf("E", "F", "G"))

        val flow = empty.combine(set1) { v1, v2 ->
            v1 + v2
        }.combine(set2) { v1, v2 ->
            v1 + v2
        }.combine(set3) { v1, v2 ->
            v1 + v2
        }

        flow.collect {
            println("Collected $it")
        }
    }

    @Test
    fun `combine sets but beware of the emptyFlow()`() = runBlocking {
        val empty = emptyFlow<Set<String>>()
        val set1 = flowOf(setOf("A", "B", "C"))
        val set2 = flowOf(setOf("C", "D", "E"))
        val set3 = flowOf(setOf("E", "F", "G"))

        val flow = empty.combine(set1) { v1, v2 ->
            v1 + v2
        }.combine(set2) { v1, v2 ->
            v1 + v2
        }.combine(set3) { v1, v2 ->
            v1 + v2
        }

        flow.collect {
            println("Collected $it")
        }
    }

    @Test
    fun `even in the end, emptyFlow() is evil`() = runBlocking {
        val empty = flowOf(emptySet<String>())
        val set1 = flowOf(setOf("A", "B", "C"))
        val set2 = flowOf(setOf("C", "D", "E"))
        val set3 = flowOf(setOf("E", "F", "G"))

        val flow = empty.combine(set1) { v1, v2 ->
            v1 + v2
        }.combine(set2) { v1, v2 ->
            v1 + v2
        }.combine(set3) { v1, v2 ->
            v1 + v2
        }.combine(emptyFlow<Set<String>>()) { v1, v2 ->
            v1 + v2
        }

        flow.collect {
            println("Collected $it")
        }
    }
}