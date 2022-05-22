package com.apjake.ayuugamemanager.ui.transaction

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apjake.ayuugamemanager.model.RequestTransaction
import com.apjake.ayuugamemanager.model.ResponseUsers
import com.apjake.ayuugamemanager.model.Transaction
import com.apjake.ayuugamemanager.model.User
import com.apjake.ayuugamemanager.retrofit.AYuuApi
import com.apjake.ayuugamemanager.retrofit.RetrofitClient
import com.apjake.ayuugamemanager.ui.transaction.TransactionResult.Companion.INVALID_AMOUNT
import com.apjake.ayuugamemanager.ui.transaction.TransactionResult.Companion.INVALID_DESTINATION
import com.apjake.ayuugamemanager.ui.transaction.TransactionResult.Companion.INVALID_SOURCE
import com.apjake.ayuugamemanager.ui.transaction.TransactionResult.Companion.INVALID_TYPE
import com.apjake.ayuugamemanager.ui.transaction.TransactionResult.Companion.LOADING
import com.apjake.ayuugamemanager.ui.transaction.TransactionResult.Companion.READY
import com.apjake.ayuugamemanager.ui.transaction.TransactionResult.Companion.RESPONSE_ERROR
import com.apjake.ayuugamemanager.ui.transaction.TransactionResult.Companion.SUCCESS
import com.apjake.ayuugamemanager.utils.UseMe
import com.apjake.ayuugamemanager.utils.toIntOrZero
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TransactionViewModel(token: String): ViewModel() {

    private var api = RetrofitClient().getApi(token)
    private var _transactionRequest = RequestTransaction(null,null, 0, "", Date())
    private var _result = MutableLiveData<TransactionResult>()
    val result = _result

    private var _users = MutableLiveData<List<User>>().apply {
        value = emptyList()
    }
    val users = _users
    private var _types = MutableLiveData<List<String>>().apply {
        value = arrayListOf("play", "win", "add")
    }
    val types = _types

    init {
        api.getAllUsers().enqueue(object : Callback<List<User>>{
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                _result.postValue(TransactionResult(status = READY))
                if(response.isSuccessful && response.code()==200 && !response.body().isNullOrEmpty()){
                    _users.postValue(response.body()!!)
                }else{
                    Log.i("Transaction", response.message()+" : "+response.code())
                    if(response.message().isNullOrBlank()){
                        setResult("Error getting users")
                    }else{
                        setResult(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                setResult(t.message.toString())
            }

        })
    }

    fun setSourceUser(data: String){
        _transactionRequest.sourceUser = data
    }

    fun setDestinationUser(data: String){
        _transactionRequest.destinationUser = data
    }

    fun setType(data: String){
        _transactionRequest.type = data
    }

    fun setAmount(data: String){
        _transactionRequest.amount = data.toIntOrZero()
    }

    private val _date= MutableLiveData<Long>().apply {
        value = UseMe.nowTimestamp()
    }
    val getDate= _date
    fun setDate(data: Long){
        _transactionRequest.createdAt = Date(data)
        _date.postValue(data)
    }

    private var _showDestination = MutableLiveData<Boolean>().apply {
        value = false
    }
    val showDestination = _showDestination
    fun setShowDestination(show: Boolean){
        if(show){
            _types.postValue( arrayListOf("loan",  "donate"))
        }
        else{
            _types.postValue( arrayListOf("play", "win", "add"))
            _transactionRequest.destinationUser = null
        }
        _showDestination.postValue(show)
    }

    fun setResult(message: String="",  status: Int=RESPONSE_ERROR){
        _result.postValue(TransactionResult(message = message, status = status))
    }

    fun addTransaction(){
        if(checkValidation()){
            api.addTransaction(_transactionRequest.sourceUser!!, _transactionRequest)
                .enqueue(object : Callback<Transaction>{
                    override fun onResponse(
                        call: Call<Transaction>,
                        response: Response<Transaction>
                    ) {
                        if(response.isSuccessful && response.code()==200 && response.body()!=null){
                            _result.postValue(
                                TransactionResult(
                                status = SUCCESS,
                                    message = "Success",
                                    transaction = response.body()!!
                                )
                            )
                        }else{
                            Log.i("Transaction", response.code().toString()+": "+response.message()+": "+response.body())
                            setResult(response.message()?:"Fail to add transaction", TransactionResult.RESPONSE_ERROR)
                        }
                    }

                    override fun onFailure(call: Call<Transaction>, t: Throwable) {
                        setResult(t.message?:"Fail to add transaction", TransactionResult.RESPONSE_ERROR)
                    }

                })
        }
    }

    private fun checkValidation(): Boolean{
        val sourceUser = _transactionRequest.sourceUser
        val destinationUser = _transactionRequest.destinationUser
        val type = _transactionRequest.type
        val amount = _transactionRequest.amount
        when {
            sourceUser.isNullOrBlank() -> {
                setResult("Source can't be empty", INVALID_SOURCE)
            }
            type.isBlank() -> {
                setResult("Type can't be empty", INVALID_TYPE)
            }
            amount==0 -> {
                setResult("Amount is require", INVALID_AMOUNT)
            }
            sourceUser==destinationUser ->{
                setResult("Source and Destination can't be the same", INVALID_DESTINATION)
            }
            else -> {
                setResult("Adding a transaction", LOADING)
                return true
            }
        }
        return false
    }

}
