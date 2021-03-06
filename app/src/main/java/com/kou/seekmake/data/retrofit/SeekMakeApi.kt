package com.kou.seekmake.data.retrofit

import com.kou.seekmake.data.common.UnsafeSSL
import com.kou.seekmake.models.SeekMake.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface SeekMakeApi {
    companion object Factory {
        fun create(): SeekMakeApi {
            val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("BASE_URL")
                    .client(UnsafeSSL.getUnsafeOkHttpClient().build())
                    .build()
            return retrofit.create(SeekMakeApi::class.java)
        }
    }

    /************************* User ********************/
    @POST("auth/register")
    fun signUp(@Body user: UserSeek): Call<SignUpResponse>

    @POST("auth/login")
    fun signIn(@Body user: UserSeek): Call<LoginResponse>

    @GET("client/{id}")
    fun getClient(@Path("id") clientID: String): Call<ClientResponse>

    @POST("mailer/reset-password")
    fun resetPwd(@Body user: UserSeek): Call<PassResetResponse>

    @PUT("client/{id}")
    fun updateProfile(@Path("id") clientID: String, @Body user: UserSeek): Call<UpdateProfileResponse>

    @PUT("client/{id}/security")
    fun updatePwd(@Path("id") clientID: String, @Body pwdRequest: PwdRequest): Call<PwdUpdateResponse>

    @Multipart
    @POST("file/upload")
    fun uploadFile(
            @Part file: MultipartBody.Part?
    ): Call<FileResponse>

    @POST("demand")
    fun submitOrder(@Header("Authorization") authToken: String, @Body order: Order): Call<OrderResponse>

    @GET("client/{id}/demand")
    fun getDemands(@Header("Authorization") authToken: String, @Path("id") clientID: String): Call<DemandsResponse>

    @PUT("client/demand/{id}")
    fun updateDemand(@Header("Authorization") authToken: String, @Path("id") demandID: String, @Body status: OrderStatus): Call<UpdateDemandResponse>


}
