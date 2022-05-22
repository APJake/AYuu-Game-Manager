package com.apjake.ayuugamemanager.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.apjake.ayuugamemanager.MainActivity
import com.apjake.ayuugamemanager.R
import com.apjake.ayuugamemanager.databinding.ActivityLoginBinding
import com.apjake.ayuugamemanager.databinding.ActivitySplashBinding
import com.apjake.ayuugamemanager.ui.home.HomeViewModel
import com.apjake.ayuugamemanager.utils.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        setContentView(binding.root)
        sessionManager = SessionManager(this)

        binding.cvBtnLogin.setOnClickListener {
            handleLogin()
        }

        viewModel.isLoading.observe(this){isLoading ->
            binding.pbLoading.visibility = if(isLoading) View.VISIBLE else View.GONE
            binding.tvLogin.visibility = if(isLoading) View.GONE else View.VISIBLE
        }
        viewModel.errorMessage.observe(this){message ->
            handleErrorMessage(message)
        }
        viewModel.isValidUsername.observe(this){isValid ->
            Log.i("Login", isValid.toString())
            binding.tilUsername.helperText = if(isValid) "" else "Required *"
        }
        viewModel.isValidPassword.observe(this){isValid ->
            binding.tilPassword.helperText = if(isValid) "" else "Required *"
        }
        viewModel.userToken.observe(this){token ->
            if(token.isNotBlank()){
                sessionManager.saveAuthToken(token)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun handleErrorMessage(message: String){
        if(message.isBlank()){
            binding.tvError.visibility = View.GONE
        }
        else{
            binding.tvError.apply {
                visibility= View.VISIBLE
                text = message
            }
        }
    }

    private fun handleLogin() {
        if(checkValidation()){
            viewModel.login(binding.edtUsername.text.toString(), binding.edtPassword.text.toString())
        }
    }

    private fun checkValidation(): Boolean {
        if(binding.edtUsername.text.isNullOrBlank()){
            viewModel.setError("Username is required", isValidUsername = false)
        }
        else if(binding.edtPassword.text.isNullOrBlank()){
            viewModel.setError("Password is required", isValidPassword = false)
        }
        else{
            return true
        }
        return false
    }
}