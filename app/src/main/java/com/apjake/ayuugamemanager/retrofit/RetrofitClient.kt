package com.apjake.ayuugamemanager.retrofit

import com.apjake.ayuugamemanager.utils.Constants.BASE_URL
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient {

    private lateinit var api: AYuuApi

    fun getApi(token: String?=null): AYuuApi {

        // Initialize ApiService if not initialized yet
        val httpClient = OkHttpClient().newBuilder().addInterceptor(Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.header("Content-Type", "application/json")
            if(token!=null)
            requestBuilder.header("auth-token", token)
            chain.proceed(requestBuilder.build())
        }).build()

        if (!::api.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            api= retrofit.create(AYuuApi::class.java)
        }

        return api
    }
}