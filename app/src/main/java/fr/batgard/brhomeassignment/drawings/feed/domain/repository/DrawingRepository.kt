package fr.batgard.brhomeassignment.drawings.feed.domain.repository

import fr.batgard.brhomeassignment.drawings.feed.domain.entities.Drawing
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.NewDrawing
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.UpdatedDrawing

interface DrawingRepository {
    suspend fun get(
        pageIndex: Int,
        pageSize: Int,
        forceRefresh: Boolean = false
    ): Result<List<Drawing>>

    suspend fun create(newDrawing: NewDrawing): Result<Drawing>
    suspend fun update(drawing: UpdatedDrawing): Result<Drawing>
}

data class DrawingPage(val pageIndex: Int, val pageSize: Int, val drawings: List<Drawing>)