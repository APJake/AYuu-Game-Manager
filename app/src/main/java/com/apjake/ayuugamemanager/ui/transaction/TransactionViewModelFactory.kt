package com.apjake.ayuugamemanager.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TransactionViewModelFactory(private val token: String): ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TransactionViewModel::class.java)){
            return TransactionViewModel(token = token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}