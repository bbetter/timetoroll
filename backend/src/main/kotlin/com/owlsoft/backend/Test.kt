import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {

    val mutableSwitch = MutableSharedFlow<Boolean>(1)
        .apply { emit(false) }

    mutableSwitch.flatMapLatest {
        var i = 0
        ticker(1000).consumeAsFlow()
            .map { i++ }
    }
        .collectLatest {
            println(it)
            if (it > 5) {
                mutableSwitch.emit(false)
            }
        }
}