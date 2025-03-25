package fr.batgard.brhomeassignment.drawings.feed.presentation.models

data class Drawing(
    val drawingId: String,
    val user: User,
    val imageUrl: String,
    val timestamp: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val offersCount: Int,
    val isLikedByUser: Boolean,
    val highestOffer: HighestOffer?
)
