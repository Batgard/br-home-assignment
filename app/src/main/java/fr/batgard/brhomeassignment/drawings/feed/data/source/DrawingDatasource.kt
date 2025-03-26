package fr.batgard.brhomeassignment.drawings.feed.data.source

import android.net.Uri
import fr.batgard.brhomeassignment.drawings.feed.data.entities.Drawing
import kotlinx.coroutines.flow.Flow

data class NewDrawing(
    val imageUri: Uri,
    val description: String,
    val createdAt: Long
)

interface DrawingDatasource {
    fun fetch(pageIndex: Int, pageSize: Int): Flow<List<Drawing>>
    suspend fun create(newDrawing: NewDrawing): Result<Drawing>
}
