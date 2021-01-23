import com.owlsoft.backend.managers.TurnTracker
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest


fun main() {
    runBlocking {

        val channel = ConflatedBroadcastChannel<String>()

        val map = mutableMapOf<String, TurnTracker>(
            "1234" to TurnTracker(2, 10, false)
        )

        launch(Dispatchers.Default) {

            channel.consumeEach {
                val tracker = map[it]
                println("TRACKER CONSUMED $it ; ${tracker.toString()}")
                tracker
                    ?.track()
                    ?.catch { println("TRACKER COMPLETED") }
                    ?.collectLatest {
                        println("$it")
                    }

            }
        }

        channel.send("1234")
        delay(3000)

//        map["1234"]?.complete()
        map["1234"] = TurnTracker(
            2,
            5,
            false
        )
        channel.send("1234")
    }
}