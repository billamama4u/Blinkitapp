import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class Notification(
    val title: String,
    val body: String
)

data class NotificationRequest(
    val to: String,
    val notification: Notification
)

interface apiInterface {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=YOUR_SERVER_KEY" // Replace with your actual server key
    )
    @POST("fcm/send")
    suspend fun sendNotification(@Body request: NotificationRequest): Response<Void>
}
