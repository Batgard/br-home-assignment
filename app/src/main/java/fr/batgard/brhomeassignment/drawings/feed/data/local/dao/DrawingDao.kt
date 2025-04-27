package fr.batgard.brhomeassignment.drawings.feed.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.DrawingEntity
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.LastUpdateTimestampEntity

@Dao
interface DrawingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(drawings: List<DrawingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drawing: DrawingEntity)

    @Query("SELECT * FROM drawings LIMIT :pageSize OFFSET (:pageIndex * :pageSize)")
    fun getDrawingsByPage(pageIndex: Int, pageSize: Int): List<DrawingEntity>

    @Upsert
    suspend fun upsertLastUpdateTimestamp(timestamp: Long)

    @Query("SELECT * FROM last_update_timestamp LIMIT 1")
    suspend fun getLastUpdateTimestamp(): LastUpdateTimestampEntity?
}
