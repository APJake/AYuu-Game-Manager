package com.apjake.ayuugamemanager.ui.transaction

import com.apjake.ayuugamemanager.model.Transaction

class TransactionResult {
    companion object{
        const val INIT = -1
        const val READY = 0
        const val LOADING = 1
        const val SUCCESS = 2

        const val NETWORK_ERROR = 51
        const val RESPONSE_ERROR = 52
        const val INVALID_SOURCE = 53
        const val INVALID_DESTINATION = 54
        const val INVALID_TYPE = 55
        const val INVALID_AMOUNT = 56
        const val INVALID_DATE = 57
    }
    var status: Int= INIT
    var message: String=""
    var transaction: Transaction?=null
    constructor(status: Int = INIT, transaction: Transaction?=null, message: String = ""){
        this.status = status
        this.transaction = transaction
        this.message = message
    }
}