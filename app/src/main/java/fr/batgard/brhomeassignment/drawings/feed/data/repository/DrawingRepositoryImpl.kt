package fr.batgard.brhomeassignment.drawings.feed.data.repository

import fr.batgard.brhomeassignment.drawings.feed.data.local.LocalDrawingDatasource
import fr.batgard.brhomeassignment.drawings.feed.data.remote.RemoteDrawingDatasource
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.Drawing
import fr.batgard.brhomeassignment.drawings.feed.domain.entities.NewDrawing
import fr.batgard.brhomeassignment.drawings.feed.domain.repository.DrawingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import java.util.concurrent.TimeUnit

class DrawingRepositoryImpl(
    private val localDrawingDatasource: LocalDrawingDatasource,
    private val remoteDrawingDatasource: RemoteDrawingDatasource,
) : DrawingRepository {

    override fun fetch(pageIndex: Int, pageSize: Int): Flow<List<Drawing>> =
        localDrawingDatasource.fetch(pageIndex, pageSize).onStart {
            val latest = localDrawingDatasource.fetch(0, 1).firstOrNull()?.firstOrNull()
            val shouldFetchFromRemote = latest == null || isDataOutdated(latest.timestamp)
            if (shouldFetchFromRemote) {
                remoteDrawingDatasource.fetch(pageIndex, pageSize).onSuccess {
                    localDrawingDatasource.add(it)
                }
            }
        }

    override suspend fun create(newDrawing: NewDrawing): Result<Drawing> {
        return remoteDrawingDatasource.add(newDrawing).onSuccess {
            localDrawingDatasource.add(listOf(it))
        }
    }

    private fun isDataOutdated(lastUpdateTimestamp: Long): Boolean {
        val tenMinutesInMillis = TimeUnit.MINUTES.toMillis(10)
        val currentTime = System.currentTimeMillis()
        return currentTime - lastUpdateTimestamp > tenMinutesInMillis
    }
}