import com.owlsoft.backend.managers.TurnTracker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() {
    runBlocking {

        val mutableList = mutableListOf<String>()
        val mutableFlow = MutableStateFlow(emptyList<String>())

        launch(Dispatchers.Default) {

            mutableFlow
                .onCompletion { println("flow completed") }
                .collectLatest {
                    println("Collected $it")
                }
        }

        mutableList.add("test")
        val isEmitted = mutableFlow.tryEmit(mutableList)
        if (isEmitted) {
            println("Emitted $mutableList")
        }
        delay(1000)
        mutableList.add("test 2")
        val isEmitted2 = mutableFlow.tryEmit(mutableList)
        if (isEmitted2) {
            println("Emitted $mutableList")
        }

        awaitCancellation()
    }
}