package com.apjake.ayuugamemanager.retrofit

import com.apjake.ayuugamemanager.model.*
import retrofit2.Call
import retrofit2.http.*

interface AYuuApi {
    @POST("auth/login")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun userLogin(@Body request: RequestLogin): Call<ResponseLogin>

    @GET("users?sort[dinger=-1")
    fun getAllUsers():Call<List<User>>

    @POST("transactions/{userId}")
    fun addTransaction(
        @Path("userId")
        userId: String,
        @Body
        request: RequestTransaction
    ): Call<Transaction>

    @POST("auth/register")
    fun userRegister(@Body request: RequestRegister): Call<User>

    @POST("auth/admin/register")
    fun userRegisterAdmin(@Body request: RequestRegister): Call<User>

}