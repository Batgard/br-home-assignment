package fr.batgard.brhomeassignment.drawings.feed.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.batgard.brhomeassignment.drawings.feed.presentation.UserInput
import fr.batgard.brhomeassignment.drawings.feed.presentation.models.FeedEntry
import fr.batgard.brhomeassignment.drawings.feed.presentation.models.FeedEntry.Drawing
import fr.batgard.brhomeassignment.drawings.feed.presentation.models.HighestOffer
import fr.batgard.brhomeassignment.drawings.feed.presentation.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface FeedUiState {
    data class Content(
        val drawings: List<FeedEntry>,
        val isBellAnimated: Boolean,
    ) : FeedUiState

    data class Error(
        val message: String,
        val showRetryCta: Boolean,
    ) : FeedUiState
}

class FeedViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<FeedUiState>(
        FeedUiState.Content(
            listOf(
                FeedEntry.Placeholder,
                FeedEntry.Placeholder,
                FeedEntry.Placeholder,
            ),
            false
        )
    )
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        loadDrawings()
    }

    fun onUserInput(userInput: UserInput) {
        when (userInput) {
            is UserInput.CommentClicked -> TODO()
            is UserInput.LikeClicked -> TODO()
            UserInput.NewDrawingClicked -> TODO()
            UserInput.NotificationClicked -> TODO()
            is UserInput.OfferClicked -> TODO()
        }
    }

    private fun loadDrawings() {
        viewModelScope.launch {
            // Simulate loading data with a delay
            delay(1000)
            try {
                // Simulate fetching data from a repository or network
                val drawings = fetchDrawings()

                // Update the UI state with the loaded data
                _uiState.update { currentState ->
                    if (currentState is FeedUiState.Content) {
                        currentState.copy(drawings = drawings)
                    } else {
                        FeedUiState.Content(drawings, false)
                    }
                }
            } catch (e: Exception) {
                // Simulate an error
                _uiState.update {
                    FeedUiState.Error(
                        message = "Failed to load data: ${e.message}",
                        showRetryCta = true,
                    )
                }
            }
        }
    }

    private fun retryCtaClicked() {
        _uiState.update { FeedUiState.Content(emptyList(), false) }
        loadDrawings()
    }

    private fun handleNewNotification() {
        _uiState.update { currentState ->
            if (currentState is FeedUiState.Content) {
                currentState.copy(isBellAnimated = true)
            } else {
                currentState
            }
        }
        viewModelScope.launch {
            delay(1000)
            _uiState.update { currentState ->
                if (currentState is FeedUiState.Content) {
                    currentState.copy(isBellAnimated = false)
                } else {
                    currentState
                }
            }
        }
    }

    private fun fetchDrawings(): List<Drawing> {
        // Simulate fetching drawings (replace this with your actual data fetching logic)
        return listOf(
            Drawing(
                drawingId = UUID.randomUUID().toString(),
                user = User("user123", "ArtMaster", "https://example.com/profiles/user456.jpg"),
                imageUrl = "https://example.com/drawings/drawing123.jpg",
                timestamp = 1678886400,
                likesCount = 42,
                commentsCount = 15,
                offersCount = 3,
                isLikedByUser = true,
                highestOffer = HighestOffer(
                    "user999",
                    50
                )
            ),
            Drawing(
                drawingId = UUID.randomUUID().toString(),
                user = User("user789", "SketchyDude", "https://example.com/profiles/user789.png"),
                imageUrl = "https://example.com/drawings/drawing456.png",
                timestamp = 1678713600,
                likesCount = 12,
                commentsCount = 3,
                offersCount = 0,
                isLikedByUser = false,
                highestOffer = null
            ),
            Drawing(
                drawingId = UUID.randomUUID().toString(),
                user = User("user123", "PicassoJr", "https://example.com/profiles/user123.jpg"),
                imageUrl = "https://example.com/drawings/drawing789.gif",
                timestamp = 1678627200,
                likesCount = 150,
                commentsCount = 3,
                offersCount = 1,
                isLikedByUser = false,
                highestOffer = HighestOffer("user222", 15)
            )
        )
    }
}