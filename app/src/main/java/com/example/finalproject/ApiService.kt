package com.example.finalproject

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("sydney/auth")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // ⬇️ return raw so we can parse flexible shapes
    @GET("dashboard/{keypass}")
    fun getDashboard(@Path("keypass") keypass: String): Call<ResponseBody>
}
