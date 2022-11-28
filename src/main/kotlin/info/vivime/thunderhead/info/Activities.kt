package info.vivime.thunderhead.info

import net.dv8tion.jda.api.entities.Activity.ActivityType

object Activities {
    const val CYCLE_LENGTH = 10000L // ten seconds
    val LIST = listOf(
        Pair(ActivityType.WATCHING, "humanity with an unblinking eye"),
        Pair(ActivityType.WATCHING, "the Scythedom, unable to comment"),
        Pair(ActivityType.LISTENING, "millions of conversations at once"),
        Pair(ActivityType.LISTENING, "the demands of humanity")
    )
}