package com.apjake.ayuugamemanager.model

import java.util.*

data class RequestTransaction (
    var sourceUser: String?,
    var destinationUser: String?,
    var amount: Int,
    var type: String,
    var createdAt: Date
        )