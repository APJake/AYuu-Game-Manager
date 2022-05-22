package com.apjake.ayuugamemanager.ui.register

class RegisterResult(
    var status: Int = READY,
    var message: String = ""
) {
    companion object{
        const val READY = 0
        const val LOADING = 1
        const val SUCCESS = 2

        const val NETWORK_ERROR = 51
        const val RESPONSE_ERROR = 52
        const val INVALID_USERNAME = 53
        const val INVALID_NAME = 54
        const val INVALID_NICKNAME = 55
        const val INVALID_PASSWORD = 56
        const val PASSWORD_NOT_MATCH = 57
    }
}