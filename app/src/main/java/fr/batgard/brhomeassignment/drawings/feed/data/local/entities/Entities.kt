package fr.batgard.brhomeassignment.drawings.feed.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawings")
data class DrawingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val drawingId: String,
    @Embedded val user: UserEntity,
    val imageUrl: String,
    val timestamp: Long,
    val likesCount: Int,
    val commentsCount: Int,
    val offersCount: Int,
    val isLikedByUser: Boolean,
    @Embedded(prefix = "highestOffer_") val highestOffer: HighestOfferEntity? = null
)

@Entity
data class UserEntity(
    @PrimaryKey val userId: String,
    val username: String,
    val profileImageUrl: String
)

@Entity
data class HighestOfferEntity(
    @PrimaryKey val userId: String,
    val amount: Int
)