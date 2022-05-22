package com.apjake.ayuugamemanager.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("_id")
    val _id: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("dinger")
    val dinger: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("createdAt")
    val createdAt: String,
){
    override fun toString(): String {
        return "${this.nickname} - ${this.username}"
    }
    fun isEqual(anotherUser: User):Boolean{
        return isEqual(anotherUser.toString())
    }
    fun isEqual(userString: String): Boolean{
        return this.toString()==userString
    }
}