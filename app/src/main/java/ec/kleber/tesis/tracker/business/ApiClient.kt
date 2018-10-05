package ec.kleber.tesis.tracker.business

import ec.kleber.tesis.tracker.BuildConfig
import ec.kleber.tesis.tracker.data.ApiSync
import ec.kleber.tesis.tracker.data.ApiSyncResponse
import ec.kleber.tesis.tracker.data.ApiUser
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import com.google.gson.GsonBuilder
import com.google.gson.Gson



interface ApiClient {

    companion object {
        fun newInstance(credential: String): ApiClient {
            val okHttpClient = OkHttpClient.Builder()
                    .authenticator { _, response ->
                        return@authenticator if (response.priorResponse() == null)
                            response.request().newBuilder().header("Authorization", credential).build()
                        else null
                    }
                    .build()

            val gson = GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                    .create()

            val retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BuildConfig.API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()

            return retrofit.create(ApiClient::class.java)
        }
    }

    @GET("api/user")
    fun getCurrentUser(): Call<ApiUser>

    @POST("api/location")
    fun postLocationUpdates(@Body locations: List<ApiSync>): Call<ApiSyncResponse>
}