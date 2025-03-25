package fr.batgard.brhomeassignment.drawings.feed.presentation

// Sealed class for user inputs
sealed interface UserInput {
    data class LikeClicked(val drawingId: String) : UserInput
    data class CommentClicked(val drawingId: String) : UserInput
    data class OfferClicked(val drawingId: String) : UserInput
    object NotificationClicked : UserInput
    object NewDrawingClicked : UserInput
}