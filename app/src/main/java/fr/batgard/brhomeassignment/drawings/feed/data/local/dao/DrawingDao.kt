package fr.batgard.brhomeassignment.drawings.feed.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.DrawingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrawingDao {
    @Query("SELECT * FROM drawings")
    fun getAllDrawings(): Flow<List<DrawingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(drawings: List<DrawingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drawing: DrawingEntity)

    @Query("SELECT * FROM drawings LIMIT :pageSize OFFSET (:pageIndex * :pageSize)")
    fun getDrawingsByPage(pageIndex: Int, pageSize: Int): Flow<List<DrawingEntity>>
}
