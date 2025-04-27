package fr.batgard.brhomeassignment.drawings.feed.domain.entities

data class Drawing(
    val drawingId: String,
    val description: String,
    val user: User,
    val imageUrl: String,
    val lastUpdatedAt: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val offersCount: Int,
    val isLikedByUser: Boolean,
    val highestOffer: HighestOffer?,
)