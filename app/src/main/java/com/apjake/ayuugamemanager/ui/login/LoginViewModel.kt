package com.apjake.ayuugamemanager.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apjake.ayuugamemanager.model.RequestLogin
import com.apjake.ayuugamemanager.model.ResponseLogin
import com.apjake.ayuugamemanager.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {

    private val _errorMessage = MutableLiveData<String>().apply {
        value = ""
    }
    private val _userToken = MutableLiveData<String>().apply {
        value = ""
    }
    private val _validUsername = MutableLiveData<Boolean>().apply {
        value = true
    }
    private val _validPassword = MutableLiveData<Boolean>().apply {
        value = true
    }
    private val _loading = MutableLiveData<Boolean>().apply {
        value = false
    }
    val userToken: LiveData<String> = _userToken
    private fun setUserToken(token: String){
        _userToken.postValue(token)
    }
    val errorMessage: LiveData<String> = _errorMessage
    private fun setErrorMessage(message: String){
        _errorMessage.postValue(message)
    }
    val isValidUsername: LiveData<Boolean> = _validUsername
    private fun setValidUsername(valid: Boolean){
        _validUsername.postValue(valid)
    }

    val isValidPassword: LiveData<Boolean> = _validPassword
    private fun setValidPassword(valid: Boolean){
        _validPassword.postValue(valid)
    }

    val isLoading: LiveData<Boolean> = _loading
    private fun setLoading(valid: Boolean){
        _loading.postValue(valid)
    }
    fun setError(message: String, isValidUsername: Boolean =true, isValidPassword: Boolean =true) {
        setErrorMessage(message)
        setValidUsername(isValidUsername)
        setValidPassword(isValidPassword)
    }
    private fun clearError(){
        setError("")
    }

    fun login(username: String, password: String){
        setLoading(true)
        clearError()
        val request = RequestLogin(
            username = username,
            password = password
        )
        RetrofitClient().getApi().userLogin(request).enqueue(object : Callback<ResponseLogin>{
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                setLoading(false)
                if(response.isSuccessful && response.code()==200 && response.body()?.accessToken!=null){
                    setUserToken(response.body()!!.accessToken!!)
                }else{
                    if(response.body()?.error==null){
                        setError("Username or password is incorrect")
                    }else{
                        setError(response.body()!!.error!!.message)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                setLoading(false)
                setError(t.message.toString())
            }

        })
    }
}