package org.kinecosystem.kinit.network

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface BackupApi {

    @GET("backup/hints")
    fun getHints(): Call<HintsResponse>

    @POST("/user/backup/hints")
    fun updateHints(@Header(USER_HEADER_KEY) userId: String, @Header(AUTH_TOKEN_HEADER) authToken: String, @Body hints: Hints): Call<StatusResponse>

    @POST("/user/email_backup")
    fun sendBackUpEmail(@Header(USER_HEADER_KEY) userId: String, @Header(AUTH_TOKEN_HEADER) authToken: String, @Body data: BackupData): Call<StatusResponse>

    data class HintsResponse(
            @SerializedName("hints") val hints: List<BackUpHint>)

    data class BackupData(
            @SerializedName("to_address") val emailAddress: String,
            @SerializedName("enc_key") val key: String)

    data class BackUpHint(
            @SerializedName("id") val id: Int,
            @SerializedName("text") val hint: String)


    data class Hints(@SerializedName("hints") val hints: List<Int>)

    data class StatusResponse(@SerializedName("status") val status: String)

}