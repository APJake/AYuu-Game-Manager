package com.apjake.ayuugamemanager.utils

import com.auth0.android.jwt.JWT

fun String.isInt():Boolean = this.toIntOrNull()!=null

fun String.isNegative(): Boolean = this.toIntOrZero()<0

fun String.toIntOrZero(): Int = this.toIntOrNull()?:0

fun String.decryptToUserRole(): String? = JWT(this).getClaim("role").asString()