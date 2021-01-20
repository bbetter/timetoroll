import com.owlsoft.backend.data.Character
import com.owlsoft.backend.data.Encounter
import com.owlsoft.backend.data.LocalEncountersDataSource
import com.owlsoft.backend.managers.EncountersManager
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
    val manager = EncountersManager(LocalEncountersDataSource)

    val encounter = Encounter(
        "abcde", "1", 60, listOf(
            Character("1", "Archie", 1, 1),
        )
    )
    LocalEncountersDataSource.add(encounter)
    manager.listenToTrackerEvents()
}