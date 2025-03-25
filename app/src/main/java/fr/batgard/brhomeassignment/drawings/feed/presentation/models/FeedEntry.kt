package fr.batgard.brhomeassignment.drawings.feed.presentation.models

sealed interface FeedEntry {
    object Placeholder: FeedEntry
    data class Drawing(
        val drawingId: String,
        val user: User,
        val imageUrl: String,
        val timestamp: Long, // TODO: Don't expose it as such. It should be a formatted string: Either a date or an elapsed time
        val likesCount: Int,
        val commentsCount: Int,
        val offersCount: Int,
        val isLikedByUser: Boolean,
        val highestOffer: HighestOffer?
    ): FeedEntry
}
