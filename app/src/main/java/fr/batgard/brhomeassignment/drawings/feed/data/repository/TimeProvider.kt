package fr.batgard.brhomeassignment.drawings.feed.data.repository

fun interface TimeProvider {
    fun getCurrentTimeMillis(): Long
}
