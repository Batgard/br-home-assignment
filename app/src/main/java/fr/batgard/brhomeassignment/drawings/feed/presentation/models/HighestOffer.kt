package fr.batgard.brhomeassignment.drawings.feed.presentation.models

data class HighestOffer(
    val userId: String,
    val amount: Int,
    // TODO: Should I add the user name so that we can display their profile pic?
)
