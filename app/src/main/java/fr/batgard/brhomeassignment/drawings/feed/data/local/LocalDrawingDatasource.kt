package fr.batgard.brhomeassignment.drawings.feed.data.local

import fr.batgard.brhomeassignment.drawings.feed.domain.entities.Drawing
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.HighestOffer
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.User
import fr.batgard.brhomeassignment.drawings.feed.data.local.dao.DrawingDao
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.DrawingEntity
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocalDrawingDatasource {
    fun fetch(pageIndex: Int, pageSize: Int): Flow<List<Drawing>>
    suspend fun add(drawings: List<Drawing>): Result<Unit>
}

class LocalDrawingDatasourceImpl( // For simplicity, we keep both the interface and the implementation in the same file
    private val drawingDao: DrawingDao
) : LocalDrawingDatasource {
    override fun fetch(pageIndex: Int, pageSize: Int): Flow<List<Drawing>> {
        return drawingDao.getDrawingsByPage(pageIndex, pageSize).map { drawingEntities ->
            drawingEntities.map { drawingEntity ->
                drawingEntity.toDrawing()
            }
        }
    }

    override suspend fun add(drawings: List<Drawing>): Result<Unit> {
        return runCatching {
            val drawingEntity = drawings.map { it.toDrawingEntity() }
            drawingDao.insertAll(drawingEntity)
        }
    }

    private fun DrawingEntity.toDrawing(): Drawing {
        return Drawing(
            drawingId = drawingId,
            user = User(user.userId, user.username, user.profileImageUrl),
            imageUrl = imageUrl,
            timestamp = timestamp,
            likesCount = likesCount,
            commentsCount = commentsCount,
            offersCount = offersCount,
            isLikedByUser = isLikedByUser,
            highestOffer = highestOffer?.let {
                HighestOffer(
                    it.userId,
                    it.amount
                )
            }
        )
    }

    private fun Drawing.toDrawingEntity(): DrawingEntity {
        return DrawingEntity(
            drawingId = drawingId,
            user = UserEntity(
                userId = user.userId,
                username = user.username,
                profileImageUrl = user.profileImageUrl
            ),
            imageUrl = imageUrl,
            timestamp = timestamp,
            likesCount = likesCount,
            commentsCount = commentsCount,
            offersCount = offersCount,
            isLikedByUser = isLikedByUser
        )
    }
}
