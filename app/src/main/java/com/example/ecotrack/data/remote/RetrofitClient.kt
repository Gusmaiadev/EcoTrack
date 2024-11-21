package com.example.ecotrack.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"
    private var token: String? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
        setLevel(HttpLoggingInterceptor.Level.HEADERS)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .apply {
                    token?.let {
                        Log.d("RetrofitClient", "Token: $it")
                        header("Authorization", "Bearer $it")
                    } ?: Log.e("RetrofitClient", "Token não encontrado!")
                }
                .build()

            Log.d("RetrofitClient", "Request URL: ${request.url}")
            Log.d("RetrofitClient", "Request Headers: ${request.headers}")

            val response = chain.proceed(request)

            Log.d("RetrofitClient", "Response Code: ${response.code}")
            Log.d("RetrofitClient", "Response Message: ${response.message}")
            Log.d("RetrofitClient", "Response Body: ${response.peekBody(Long.MAX_VALUE).string()}")

            response
        }
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun setToken(newToken: String) {
        token = newToken
        Log.d("RetrofitClient", "Novo token definido: $token")
    }
}