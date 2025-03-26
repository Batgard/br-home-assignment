package fr.batgard.brhomeassignment.drawings.feed.domain.repository

import fr.batgard.brhomeassignment.drawings.feed.domain.entities.NewDrawing
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.Drawing
import kotlinx.coroutines.flow.Flow

interface DrawingRepository {
    fun fetch(pageIndex: Int, pageSize: Int): Flow<List<Drawing>>
    suspend fun create(newDrawing: NewDrawing): Result<Drawing>
}