package com.apjake.ayuugamemanager.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.apjake.ayuugamemanager.R
import com.apjake.ayuugamemanager.databinding.FragmentHomeBinding
import com.apjake.ayuugamemanager.databinding.FragmentRegisterBinding
import com.apjake.ayuugamemanager.model.User
import com.apjake.ayuugamemanager.ui.home.HomeFragment
import com.apjake.ayuugamemanager.ui.login.LoginActivity
import com.apjake.ayuugamemanager.utils.SessionManager
import com.apjake.ayuugamemanager.utils.decryptToUserRole

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModelFactory: RegisterViewModelFactory
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: RegisterViewModel
    private var token: String? = null
    private var currentUserRole: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sessionManager = SessionManager(requireContext())
        token = sessionManager.fetchAuthToken()
        if(token.isNullOrBlank()){
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            return null
        }
        currentUserRole = token?.decryptToUserRole()
        viewModelFactory = RegisterViewModelFactory(token!!)
        viewModel = ViewModelProvider(this, viewModelFactory)[RegisterViewModel::class.java]
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        handleUI()
        viewModelObservers()

        return binding.root
    }

    private fun viewModelObservers() {
        viewModel.result.observe(this){ result->
            val status = result.status
            val message = result.message
            if(status==RegisterResult.SUCCESS){
                goToHome()
            }
            handleMessage(status, message)
            handleLoading(status==RegisterResult.LOADING)
        }
    }

    private fun goToHome() {
        findNavController().navigate(R.id.action_nav_register_to_nav_home)
    }

    private fun handleLoading(isLoading: Boolean) {
        if(isLoading){
            binding.tvRegister.visibility = View.GONE
            binding.pbLoading.visibility = View.VISIBLE
        }else{
            binding.tvRegister.visibility = View.VISIBLE
            binding.pbLoading.visibility = View.GONE
        }
    }

    private fun handleMessage(status: Int, message: String) {
        binding.tilUsername.error = null
        binding.tilName.error = null
        binding.tilNickname.error = null
        binding.tilPassword.error = null

        binding.tvError.text = message
        if(status>=50){ // errors
            binding.tvError.visibility = View.VISIBLE
            when(status){
                RegisterResult.INVALID_USERNAME ->{
                    binding.tilUsername.error = message
                    binding.tilUsername.requestFocus()
                }
                RegisterResult.INVALID_NAME ->{
                    binding.tilName.error = message
                    binding.tilName.requestFocus()
                }
                RegisterResult.INVALID_NICKNAME ->{
                    binding.tilNickname.error = message
                    binding.tilNickname.requestFocus()
                }
                RegisterResult.INVALID_PASSWORD ->{
                    binding.tilPassword.error = message
                    binding.tilPassword.requestFocus()
                }
            }
        }
        else{
            binding.tvError.visibility = View.GONE
        }
    }

    private fun handleUI(){
        if(currentUserRole=="admin"){
            val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.roles,android.R.layout.simple_spinner_dropdown_item)
            binding.actRole.setAdapter(adapter)
            binding.tilRole.visibility = View.VISIBLE
            binding.actRole.setOnItemClickListener { parent, _, position, _ ->
                val role = parent.getItemAtPosition(position)
                if(role is String){
                    viewModel.setRole(role)
                }
            }
        }else{
            binding.tilRole.visibility = View.GONE
        }
        binding.edtUsername.addTextChangedListener { text ->
            viewModel.setUsername(text.toString())
        }
        binding.edtName.addTextChangedListener { text ->
            viewModel.setName(text.toString())
        }
        binding.edtNickname.addTextChangedListener { text ->
            viewModel.setNickname(text.toString())
        }
        binding.edtPassword.addTextChangedListener { text ->
            viewModel.setPassword(text.toString())
        }
        binding.cvBtnRegister.setOnClickListener {
            viewModel.doRegister()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}