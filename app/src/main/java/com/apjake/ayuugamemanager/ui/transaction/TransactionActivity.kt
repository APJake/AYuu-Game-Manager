package com.apjake.ayuugamemanager.ui.transaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.apjake.ayuugamemanager.R
import com.apjake.ayuugamemanager.databinding.ActivityTransactionBinding
import com.apjake.ayuugamemanager.model.RequestTransaction
import com.apjake.ayuugamemanager.model.User
import com.apjake.ayuugamemanager.ui.login.LoginActivity
import com.apjake.ayuugamemanager.utils.SessionManager
import com.apjake.ayuugamemanager.utils.UseMe
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

class TransactionActivity : AppCompatActivity() {


    private lateinit var datePicker: MaterialDatePicker<Long>

    private val sessionManager: SessionManager by lazy {
        SessionManager(this)
    }
    private lateinit var binding: ActivityTransactionBinding
    private lateinit var viewModelFactory: TransactionViewModelFactory
    private lateinit var viewModel: TransactionViewModel

    private var users: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)

        val token = sessionManager.fetchAuthToken()
        if(token.isNullOrBlank()){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        viewModelFactory = TransactionViewModelFactory(token!!)
        viewModel = ViewModelProvider(this, viewModelFactory)[TransactionViewModel::class.java]
        setContentView(binding.root)

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(UseMe.nowTimestamp())
            .build()
        datePicker.addOnPositiveButtonClickListener {
            viewModel.setDate(it)
        }

        viewModelObservers()
        handleUI()
    }

    private fun handleUI() {
        binding.edtDate.setText(UseMe.getTodayDateOnlyString())
        binding.edtDate.setOnClickListener {
            showDatePicker()
        }
        binding.edtDate.setOnFocusChangeListener { _, b ->
            if(b) showDatePicker()
        }

        binding.cbDestination.setOnCheckedChangeListener { _, check ->
            viewModel.setShowDestination(check)
            binding.actType.setText("")
        }
        binding.rgAmountType.setOnCheckedChangeListener { group, id ->
            val amount = binding.edtAmount.text.toString().toIntOrNull()?:0
            if(id == R.id.rbOutcome){
                if(amount>0) binding.edtAmount.setText((amount*(-1)).toString())
            }else{
                if(amount<0) binding.edtAmount.setText((amount*(-1)).toString())
            }
        }

        binding.actType.addTextChangedListener { text ->
            viewModel.setType(text.toString())
        }
        binding.edtAmount.addTextChangedListener { text ->
            viewModel.setAmount(text.toString())
        }
        binding.actSource.setOnItemClickListener { parent, _, position, _ ->
            val user = parent.getItemAtPosition(position)
            if(user is User){
                viewModel.setSourceUser(user._id)
            }
        }
        binding.actDestination.setOnItemClickListener { parent, _, position, _ ->
            val user = parent.getItemAtPosition(position)
            if(user is User){
                viewModel.setDestinationUser(user._id)
            }
        }
        binding.cvBtnAdd.setOnClickListener {
            viewModel.addTransaction()
        }
    }

    private fun showDatePicker() {
        datePicker.show(supportFragmentManager,"MATERIAL_DATE_PICKER")
    }

    private fun viewModelObservers(){
        viewModel.getDate.observe(this){ ts ->
            binding.edtDate.setText(UseMe.getDateOnlyString(ts))
        }
        viewModel.users.observe(this){users ->
            if(!users.isNullOrEmpty()){
                this.users = users
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)
                binding.actSource.setAdapter(adapter)
                binding.actDestination.setAdapter(adapter)
            }
        }
        viewModel.types.observe(this){types ->
            if(!types.isNullOrEmpty()){
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, types)
                binding.actType.setAdapter(adapter)
            }
        }
        viewModel.showDestination.observe(this){show->
            binding.tilDestination.visibility = if(show) View.VISIBLE else View.GONE
        }
        viewModel.result.observe(this){ result ->
            val status = result.status
            handleLoadingUsers(status==TransactionResult.INIT)
            handleAddLoading(status==TransactionResult.LOADING)
            handleMessage(result.message, status)
        }
    }

    private fun handleMessage(message: String, status: Int){
        binding.tilSource.error = null
        binding.tilAmount.error = null
        binding.tilType.error = null
        binding.tilDestination.error = null

        if(status>=50){
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.text = message
            binding.tvError.setTextColor(ContextCompat.getColor(this,R.color.error))
            when(status){
                TransactionResult.INVALID_SOURCE -> {
                    binding.tilSource.requestFocus()
                    binding.tilSource.error = message
                }
                TransactionResult.INVALID_AMOUNT -> {
                    binding.tilAmount.requestFocus()
                    binding.tilAmount.error = message
                }
                TransactionResult.INVALID_TYPE -> {
                    binding.tilType.requestFocus()
                    binding.tilType.error = message
                }
                TransactionResult.INVALID_DESTINATION -> {
                    binding.tilDestination.requestFocus()
                    binding.tilDestination.error = message
                }
            }
        }
        else if(status==TransactionResult.SUCCESS){
            binding.tvError.visibility = View.VISIBLE
            binding.tvError.setTextColor(ContextCompat.getColor(this,R.color.secondary))
            binding.tvError.text = message
            binding.actSource.setText("")
            binding.actDestination.setText("")
            viewModel.setSourceUser("")
            viewModel.setDestinationUser("")
        }
        else{
            binding.tvError.visibility = View.GONE
        }
    }

    private fun handleAddLoading(isLoading: Boolean) {
        if(isLoading){
            binding.pbLoadingOnBtn.visibility = View.VISIBLE
            binding.tvAdd.visibility = View.GONE
        }else{
            binding.pbLoadingOnBtn.visibility = View.GONE
            binding.tvAdd.visibility = View.VISIBLE
        }
        binding.cvBtnAdd.isEnabled = !isLoading
        binding.edtDate.isEnabled = !isLoading
        binding.edtAmount.isEnabled = !isLoading
        binding.tilDestination.isEnabled = !isLoading
        binding.tilSource.isEnabled = !isLoading
        binding.tilType.isEnabled = !isLoading
        binding.tilDate.isEnabled = !isLoading
        binding.cbDestination.isEnabled = !isLoading
        binding.rgAmountType.isEnabled = !isLoading
    }

    private fun handleLoadingUsers(isLoading: Boolean){
        binding.svHolder.visibility = if(isLoading) View.GONE else View.VISIBLE
        binding.pbLoading.visibility = if(isLoading) View.VISIBLE else View.GONE
    }

}