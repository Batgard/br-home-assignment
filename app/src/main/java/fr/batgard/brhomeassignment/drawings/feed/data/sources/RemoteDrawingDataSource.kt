package fr.batgard.brhomeassignment.drawings.feed.data.sources

import fr.batgard.brhomeassignment.drawings.feed.data.entities.Drawing
import fr.batgard.brhomeassignment.drawings.feed.data.entities.HighestOffer
import fr.batgard.brhomeassignment.drawings.feed.data.entities.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class RemoteDrawingDatasource : DrawingDatasource {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override fun fetch(pageIndex: Int, pageSize: Int): Flow<List<Drawing>> = flow {
        try {
            val response: HttpResponse =
                client.get("https://fr.batgard.soisvrai-home-assignment/drawings/") {
                    //You should replace that with the real parameters
                    url {
                        parameters.append("page", pageIndex.toString())
                        parameters.append("size", pageSize.toString())
                    }
                }

            if (response.status == HttpStatusCode.OK) {
                val drawingResponse: DrawingResponse = response.body()
                emit(drawingResponse.data.map { it.toDrawing() })
            } else {
                throw Exception("Failed to load data: ${response.status}")
            }
        } catch (e: Exception) {
            throw Exception("Failed to load data: ${e.message}")
        }
    }

    override suspend fun create(newDrawing: NewDrawing): Result<Drawing> {
        return runCatching {
            val response: HttpResponse =
                client.post("https://fr.batgard.soisvrai-home-assignment/drawings/") {
                    contentType(io.ktor.http.ContentType.MultiPart.FormData)
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("image", newDrawing.imageUri.toString())
                                append("description", newDrawing.description)
                                append("createdAt", newDrawing.createdAt)
                            }
                        ))

                }
            response.body()
        }
    }
}

@Serializable
data class UserResponse(
    @SerialName("userId") val userId: String,
    @SerialName("username") val username: String,
    @SerialName("profileImageUrl") val profileImageUrl: String
)

@Serializable
data class HighestOfferResponse(
    @SerialName("userId") val userId: String,
    @SerialName("amount") val amount: Int
)

@Serializable
data class DrawingResponseItem(
    @SerialName("drawingId") val drawingId: String,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("likesCount") val likesCount: Int,
    @SerialName("commentsCount") val commentsCount: Int,
    @SerialName("offersCount") val offersCount: Int,
    @SerialName("isLikedByUser") val isLikedByUser: Boolean,
    @SerialName("highestOffer") val highestOffer: HighestOfferResponse?,
    @SerialName("user") val user: UserResponse
) {
    fun toDrawing(): Drawing {
        return Drawing(
            drawingId = drawingId,
            imageUrl = imageUrl,
            timestamp = timestamp,
            likesCount = likesCount,
            commentsCount = commentsCount,
            offersCount = offersCount,
            isLikedByUser = isLikedByUser,
            highestOffer = highestOffer?.let { HighestOffer(it.userId, it.amount) },
            user = User(user.userId, user.username, user.profileImageUrl)
        )
    }
}

@Serializable
data class DrawingResponse(
    @SerialName("data") val data: List<DrawingResponseItem>,
    @SerialName("pagination") val pagination: PaginationResponse
)

@Serializable
data class PaginationResponse(
    @SerialName("currentPage") val currentPage: Int,
    @SerialName("pageSize") val pageSize: Int,
    @SerialName("totalItems") val totalItems: Int,
    @SerialName("totalPages") val totalPages: Int,
    @SerialName("hasNextPage") val hasNextPage: Boolean,
    @SerialName("hasPreviousPage") val hasPreviousPage: Boolean
)