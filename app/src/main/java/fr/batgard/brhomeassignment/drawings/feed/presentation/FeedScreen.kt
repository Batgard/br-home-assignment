package fr.batgard.brhomeassignment.drawings.feed.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.batgard.brhomeassignment.R
import fr.batgard.brhomeassignment.drawings.feed.presentation.models.Drawing
import fr.batgard.brhomeassignment.drawings.feed.presentation.models.HighestOffer
import fr.batgard.brhomeassignment.drawings.feed.presentation.models.User
import java.util.UUID

// Sealed class for user inputs
sealed class UserInput {
    data class LikeClicked(val drawingId: String) : UserInput()
    data class CommentClicked(val drawingId: String) : UserInput()
    data class OfferClicked(val drawingId: String) : UserInput()
    object NotificationClicked : UserInput()
    object NewDrawingClicked : UserInput()
}

@Composable
fun FeedScreen(
    drawings: List<Drawing>,
    onUserInput: (UserInput) -> Unit,
    isBellAnimated: Boolean
) {
    Scaffold(
        topBar = {
            FeedTopAppBar(
                onNotificationClicked = { onUserInput(UserInput.NotificationClicked) },
                isBellAnimated = isBellAnimated
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(drawings) { drawing ->
                    DrawingItem(
                        drawing = drawing,
                        onUserInput = onUserInput
                    )
                }
            }

            Button(
                onClick = { onUserInput(UserInput.NewDrawingClicked) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text(text = "New Drawing")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopAppBar(onNotificationClicked: () -> Unit, isBellAnimated: Boolean) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Drawings Feed",
                fontWeight = FontWeight.Bold,
            )
        },
        actions = {
            NotificationBell(
                onNotificationClicked = onNotificationClicked,
                isBellAnimated = isBellAnimated
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    )
}

@Composable
fun DrawingItem(
    drawing: Drawing,
    onUserInput: (UserInput) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(user = drawing.user)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = drawing.user.username,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(drawing.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "Drawing Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        drawing.highestOffer?.let {
            HighestOffer(highestOffer = it)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LikeButton(
                isLiked = drawing.isLikedByUser,
                likesCount = drawing.likesCount,
                onLikeClicked = { onUserInput(UserInput.LikeClicked(drawing.drawingId)) }
            )
            CommentButton(
                commentsCount = drawing.commentsCount,
                onCommentClicked = { onUserInput(UserInput.CommentClicked(drawing.drawingId)) }
            )

            OfferButton(
                offersCount = drawing.offersCount,
                onOfferClicked = { onUserInput(UserInput.OfferClicked(drawing.drawingId)) }
            )
        }
    }
}

@Composable
fun OfferButton(offersCount: Int, onOfferClicked: () -> Unit) {
    Row(
        modifier = Modifier.clickable { onOfferClicked() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberVectorPainter(Icons.Filled.MoreVert),
            contentDescription = "Offer",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = offersCount.toString())
    }
}

@Composable
fun HighestOffer(highestOffer: HighestOffer) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Highest Offer: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$${highestOffer.amount} by ${highestOffer.userId}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun UserAvatar(user: User) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(user.profileImageUrl)
            .crossfade(true)
            .build(),
        placeholder = rememberVectorPainter(Icons.Filled.Person),
        contentDescription = "User Avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
    )
}

@Composable
fun LikeButton(isLiked: Boolean, likesCount: Int, onLikeClicked: () -> Unit) {
    Row(
        modifier = Modifier.clickable { onLikeClicked() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = "Like",
            tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = likesCount.toString())
    }
}

@Composable
fun CommentButton(commentsCount: Int, onCommentClicked: () -> Unit) {
    Row(
        modifier = Modifier.clickable { onCommentClicked() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Add, // Fixme: Use the correct icon
            contentDescription = "Comment",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = commentsCount.toString())
    }
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    val drawings = listOf(
        Drawing(
            drawingId = UUID.randomUUID().toString(),
            user = User("user123", "ArtMaster", "https://example.com/profiles/user456.jpg"),
            imageUrl = "https://example.com/drawings/drawing123.jpg",
            timestamp = 1678886400,
            likesCount = 42,
            commentsCount = 15,
            offersCount = 3,
            isLikedByUser = true,
            highestOffer = HighestOffer("user999", 50)
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
    FeedScreen(drawings = drawings, onUserInput = {}, isBellAnimated = false)
}




