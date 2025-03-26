package fr.batgard.brhomeassignment.drawings.feed.data.local

import fr.batgard.brhomeassignment.drawings.feed.data.entities.Drawing
import fr.batgard.brhomeassignment.drawings.feed.data.entities.HighestOffer
import fr.batgard.brhomeassignment.drawings.feed.data.entities.User
import fr.batgard.brhomeassignment.drawings.feed.data.local.dao.DrawingDao
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.DrawingEntity
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.UserEntity
import fr.batgard.brhomeassignment.drawings.feed.data.source.DrawingDatasource
import fr.batgard.brhomeassignment.drawings.feed.data.source.NewDrawing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class LocalDrawingDatasource(
    private val drawingDao: DrawingDao
) : DrawingDatasource {
    override fun fetch(pageIndex: Int, pageSize: Int): Flow<List<Drawing>> {
        return drawingDao.getDrawingsByPage(pageIndex, pageSize).map { drawingEntities ->
            drawingEntities.map { drawingEntity ->
                drawingEntity.toDrawing()
            }
        }
    }

    override suspend fun create(newDrawing: NewDrawing): Result<Drawing> {
        return try {
            val drawingEntity = newDrawing.toDrawingEntity()
            drawingDao.insert(drawingEntity)
            Result.success(drawingEntity.toDrawing())
        } catch (e: Exception) {
            Result.failure(e)
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

    private fun NewDrawing.toDrawingEntity(): DrawingEntity {
        return DrawingEntity(
            drawingId = UUID.randomUUID().toString(),
            user = UserEntity(UUID.randomUUID().toString(), "currentUserName", "currentUserProfileUrl"), // FIXME: Complete with the logged in user
            imageUrl = imageUri.toString(),
            timestamp = createdAt,
            likesCount = 0,
            commentsCount = 0,
            offersCount = 0,
            isLikedByUser = false,
        )
    }
}