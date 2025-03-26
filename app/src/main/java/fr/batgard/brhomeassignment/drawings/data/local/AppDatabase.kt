package fr.batgard.brhomeassignment.drawings.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.batgard.brhomeassignment.drawings.feed.data.local.dao.DrawingDao
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.DrawingEntity
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.HighestOfferEntity
import fr.batgard.brhomeassignment.drawings.feed.data.local.entities.UserEntity

@Database(
    entities = [DrawingEntity::class, UserEntity::class, HighestOfferEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun drawingDao(): DrawingDao
}