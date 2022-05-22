package com.apjake.ayuugamemanager.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.apjake.ayuugamemanager.model.User
import com.apjake.ayuugamemanager.retrofit.RetrofitClient
import com.apjake.ayuugamemanager.ui.register.RegisterViewModel
import com.apjake.ayuugamemanager.ui.transaction.TransactionResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(token: String) : ViewModel() {

    private var api = RetrofitClient().getApi(token)

    private var _loading = MutableLiveData<Boolean>().apply {
        value = true
    }
    val isLoading = _loading

    private var _message = MutableLiveData<String>().apply {
        value = ""
    }
    val message = _message

    private var _users = MutableLiveData<List<User>>().apply {
        value = emptyList()
    }
    val users = _users
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val text: LiveData<String> = _text


    fun getUsers(){
        api.getAllUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                _loading.postValue(false)
                if(response.isSuccessful && response.code()==200 && !response.body().isNullOrEmpty()){
                    _users.postValue(response.body()!!)
                    Log.i("Users", "Here")

                }else{
                    if(response.message().isNullOrBlank()){
                        _message.postValue("Error getting users")
                    }else{
                        _message.postValue(response.message())
                    }
                    Log.i("Users", response.message())
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                _loading.postValue(false)
                _message.postValue(t.message.toString())
                Log.i("Users", t.message.toString())

            }

        })
    }
}

class HomeViewModelFactory(private val token: String): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(token = token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}