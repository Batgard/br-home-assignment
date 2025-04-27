package fr.batgard.brhomeassignment.drawings.feed.domain.entities

/**
 * Represents what you can update on a drawing
 * It appears that comments should have it's own entity as a common use case
 * is to allow the user to edit their comments
 */
data class UpdatedDrawing(
    val id: String,
    val isLikedByUser: Boolean,
    val description: String?,
    val userOffer: Int?,
    val newComment: String?,
) {

}