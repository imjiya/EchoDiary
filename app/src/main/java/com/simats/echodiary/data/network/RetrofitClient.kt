package com.simats.echodiary.data.network

import android.content.Context
import com.simats.echodiary.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://exonerate-tipper-sake.ngrok-free.dev/echodiary_backend/"
    private var apiInstance: EchoDiaryApi? = null

    fun getApi(context: Context): EchoDiaryApi {
        return apiInstance ?: synchronized(this) {
            apiInstance ?: buildRetrofit(context).create(EchoDiaryApi::class.java).also {
                apiInstance = it
            }
        }
    }

    private fun buildRetrofit(context: Context): Retrofit {
        val sessionManager = SessionManager.getInstance(context)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val path = original.url.encodedPath

            // Do not attach Authorization header to login or signup requests
            if (path.contains("auth/login.php") || path.contains("auth/signup.php")) {
                chain.proceed(original)
            } else {
                val token = sessionManager.getToken()
                if (!token.isNullOrBlank()) {
                    val authenticatedRequest = original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(authenticatedRequest)
                } else {
                    chain.proceed(original)
                }
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

