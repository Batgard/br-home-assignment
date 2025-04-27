package fr.batgard.brhomeassignment.drawings.feed.domain.entities

import android.net.Uri

data class NewDrawing(
    val imageUri: Uri,
    val description: String,
    val createdAt: Long
) {

}