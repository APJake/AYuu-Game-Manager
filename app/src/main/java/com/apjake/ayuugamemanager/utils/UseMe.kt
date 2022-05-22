package com.apjake.ayuugamemanager.utils

import java.text.SimpleDateFormat
import java.util.*

object UseMe {
    fun getTodayDateOnlyString(): String{
        return getDateOnlyString(nowTimestamp())
    }
    fun nowTimestamp(): Long{
        return System.currentTimeMillis()
    }
    fun getDateOnlyString(date: Date): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(date)
    }
    fun getDateOnlyString(timestamp: Long): String{
        return getDateOnlyString(getDate(timestamp))
    }
    fun getDate(timestamp: Long): Date{
        return Date(timestamp)
    }
}