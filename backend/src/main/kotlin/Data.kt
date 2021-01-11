import kotlinx.serialization.Serializable

@Serializable
class Participant(
    val ownerID: String,
    val name: String,
    val initiative: Int,
    val dexterity: Int
)

@Serializable
data class Encounter(
    val code: String = "",
    val ownerID: String,
    val participants: List<Participant>,
    val startTimeStamp: Long = 0
)

@Serializable
data class RoundInfo(
    val tickTime: Int,
    val activeParticipantIndex: Int,
    val roundIndex: Int,
)