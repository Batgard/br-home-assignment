package fr.batgard.brhomeassignment.drawings.feed.data.repository

import fr.batgard.brhomeassignment.drawings.feed.data.local.LocalDrawingDatasource
import fr.batgard.brhomeassignment.drawings.feed.data.remote.RemoteDrawingDatasource
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.Drawing
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.NewDrawing
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.UpdatedDrawing
import fr.batgard.brhomeassignment.drawings.feed.domain.repository.DrawingRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.TimeUnit

class DrawingRepositoryImpl(
    private val localDrawingDatasource: LocalDrawingDatasource,
    private val remoteDrawingDatasource: RemoteDrawingDatasource,
    private val timeProvider: TimeProvider = TimeProvider { System.currentTimeMillis() },
) : DrawingRepository {

    override suspend fun get(
        pageIndex: Int,
        pageSize: Int,
        forceRefresh: Boolean
    ): Result<List<Drawing>> {
        localDrawingDatasource.fetch(pageIndex, pageSize)
        val latest = localDrawingDatasource.fetch(0, 1).firstOrNull()
        val lastUpdateTimestamp =
            localDrawingDatasource.getLastUpdateTimestamp().getOrNull() ?: -1
        val shouldFetchFromRemote = latest == null || isDataOutdated(lastUpdateTimestamp)
        if (shouldFetchFromRemote) {
            remoteDrawingDatasource.fetch(pageIndex, pageSize).onSuccess {
                localDrawingDatasource.add(drawings = it.drawings)
            }
        }
        return Result.success(localDrawingDatasource.fetch(pageIndex, pageSize))
    }

    override suspend fun update(drawing: UpdatedDrawing): Result<Drawing> {
        return remoteDrawingDatasource.update(drawing).onSuccess {
            localDrawingDatasource.update(it)
        }
    }

    override suspend fun create(newDrawing: NewDrawing): Result<Drawing> {
        return remoteDrawingDatasource.add(newDrawing).onSuccess {
            localDrawingDatasource.add(listOf(it))
        }
    }

    private fun isDataOutdated(lastUpdateTimestamp: Long): Boolean {
        val tenMinutesInMillis = TimeUnit.MINUTES.toMillis(10)
        return timeProvider.getCurrentTimeMillis() - lastUpdateTimestamp > tenMinutesInMillis
    }
}
