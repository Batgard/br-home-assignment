package fr.batgard.brhomeassignment.drawings.feed.data.local

import fr.batgard.brhomeassignment.drawings.feed.data.local.dao.DrawingDao
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.DrawingEntity
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.UserEntity
import fr.batgard.brhomeassignment.drawings.feed.data.repository.TimeProvider
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.Drawing
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.HighestOffer
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.User

interface LocalDrawingDatasource {
    fun fetch(pageIndex: Int, pageSize: Int): List<Drawing>
    suspend fun add(drawings: List<Drawing>): Result<Unit>
    suspend fun update(drawing: Drawing): Result<Unit>
    suspend fun getLastUpdateTimestamp(): Result<Long>
}

// For simplicity, we keep both the interface and the implementation in the same file
class LocalDrawingDatasourceImpl(
    private val drawingDao: DrawingDao,
    private val timeProvider: TimeProvider = TimeProvider { System.currentTimeMillis() },
) : LocalDrawingDatasource {

    override fun fetch(pageIndex: Int, pageSize: Int): List<Drawing> {
        return drawingDao.getDrawingsByPage(pageIndex, pageSize).map { drawingEntities ->
            drawingEntities.toDrawing()
        }
    }

    override suspend fun add(drawings: List<Drawing>): Result<Unit> {
        return runCatching {
            val drawingEntity = drawings.map { it.toDrawingEntity() }
            drawingDao.insertAll(drawingEntity)
            drawingDao.upsertLastUpdateTimestamp(timeProvider.getCurrentTimeMillis())
        }
    }

    override suspend fun update(drawing: Drawing): Result<Unit> {
        return runCatching {
            drawingDao.insertAll(listOf(drawing.toDrawingEntity()))
        }
    }

    override suspend fun getLastUpdateTimestamp(): Result<Long> {
        return runCatching {
            drawingDao.getLastUpdateTimestamp()?.timestamp ?: 0
        }
    }

    private fun DrawingEntity.toDrawing(): Drawing {
        return Drawing(
            drawingId = drawingId,
            user = User(user.userId, user.username, user.profileImageUrl),
            imageUrl = imageUrl,
            description = description,
            lastUpdatedAt = lastUpdatedAt,
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
            description = description,
            imageUrl = imageUrl,
            lastUpdatedAt = lastUpdatedAt,
            likesCount = likesCount,
            commentsCount = commentsCount,
            offersCount = offersCount,
            isLikedByUser = isLikedByUser
        )
    }
}
