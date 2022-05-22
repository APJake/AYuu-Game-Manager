package com.apjake.ayuugamemanager.model

import com.google.gson.annotations.SerializedName

data class ResponseLogin (
    @SerializedName("data")
    val data: User?,

    @SerializedName("error")
    val error: Error?,

    @SerializedName("accessToken")
    val accessToken: String?,
    )