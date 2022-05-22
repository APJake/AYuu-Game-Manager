package com.apjake.ayuugamemanager.ui.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apjake.ayuugamemanager.model.RequestRegister
import com.apjake.ayuugamemanager.model.User
import com.apjake.ayuugamemanager.retrofit.RetrofitClient
import com.apjake.ayuugamemanager.ui.transaction.TransactionResult
import com.apjake.ayuugamemanager.ui.transaction.TransactionViewModel
import com.auth0.android.jwt.JWT
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val token: String): ViewModel() {

    private var api = RetrofitClient().getApi(token)
    private val requestRegister: RequestRegister= RequestRegister()
    private val _result= MutableLiveData<RegisterResult>().apply {
        value = RegisterResult()
    }
    val result = _result
    fun setResult(status: Int=RegisterResult.RESPONSE_ERROR, message: String=""){
        _result.postValue(RegisterResult(status, message))
    }
    fun setUsername(data:String){
        requestRegister.username = data
    }
    fun setNickname(data:String){
        requestRegister.nickname = data
    }
    fun setName(data:String){
        requestRegister.name = data
    }
    fun setPassword(data:String){
        requestRegister.password = data
    }
    fun setRole(data:String){
        requestRegister.role = data
    }
    fun doRegister(){
        if(checkValidation()){
            val jwt = JWT(token)
            val currentUserRole = jwt.getClaim("role").asString()
            var call = api.userRegister(requestRegister)
            if(currentUserRole=="admin"){
                call = api.userRegisterAdmin(requestRegister)
            }
            call.enqueue(object : Callback<User>{
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if(response.isSuccessful && response.code()==200 && response.body()!=null){
                        setResult(status = RegisterResult.SUCCESS, message = "Success")
                    }else{
                        setResult(message = "Username already exits",status= TransactionResult.RESPONSE_ERROR)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    setResult(message = t.message?:"Fail to register (Server error)", status = TransactionResult.RESPONSE_ERROR)
                    Log.i("Register", t.printStackTrace().toString())

                }

            })
        }
    }

    private fun checkValidation(): Boolean {
        val username = requestRegister.username
        val name = requestRegister.name
        val nickname = requestRegister.nickname
        val password = requestRegister.password
        val usernameRegex = Regex("^[a-z0-9._-]{3,}\$")
        val passwordRegex = Regex(".{4,}")
        if(username.isNullOrBlank()){
            setResult(RegisterResult.INVALID_USERNAME, "Username can't be empty")
        }
        else if(!usernameRegex.containsMatchIn(username)){
            setResult(RegisterResult.INVALID_USERNAME, "Username should only be letters, numbers, (.) and (_) and at least 3 characters")
        }
        else if(password.isNullOrBlank()){
            setResult(RegisterResult.INVALID_PASSWORD, "Password can't be empty")
        }
        else if(!passwordRegex.containsMatchIn(password)){
            setResult(RegisterResult.INVALID_PASSWORD, "Password should have at least 4 characters")
        }
        else if(name.isNullOrBlank()){
            setResult(RegisterResult.INVALID_NAME, "Name can't be empty")
        }
        else if(nickname.isNullOrBlank()){
            setResult(RegisterResult.INVALID_NICKNAME, "Nickname can't be empty")
        }
        else{
            setResult(message = "Please wait", status = RegisterResult.LOADING)
            return true
        }
        return false
    }
}

class RegisterViewModelFactory(private val token: String): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(token = token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}