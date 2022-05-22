package com.apjake.ayuugamemanager.model

data class Transaction(
    val _id: String,
    val amount: Int,
    val createdAt: String,
    val destinationUser: String?,
    val sourceUser: String,
    val total: Int,
    val type: String,
    val updatedAt: String
)