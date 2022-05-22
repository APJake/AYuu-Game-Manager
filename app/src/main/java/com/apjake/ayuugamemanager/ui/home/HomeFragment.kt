package com.apjake.ayuugamemanager.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.apjake.ayuugamemanager.databinding.FragmentHomeBinding
import com.apjake.ayuugamemanager.ui.login.LoginActivity
import com.apjake.ayuugamemanager.ui.transaction.TransactionActivity
import com.apjake.ayuugamemanager.utils.SessionManager




class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModelFactory
    private var _binding: FragmentHomeBinding? = null
    private lateinit var adapter: AYuuAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sessionManager = SessionManager(requireContext())
        val token = sessionManager.fetchAuthToken()
        if(token==null){
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            return null
        }
        viewModelFactory = HomeViewModelFactory(token)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.fab.setOnClickListener { view ->
            startActivity(Intent(requireContext(), TransactionActivity::class.java))
        }

        handleUI()
        handleObservers()

        return binding.root
    }

    private fun handleUI(){
        // SwipeRefreshLayout
        // SwipeRefreshLayout
        binding.refLayout.setOnRefreshListener(this)

        binding.refLayout.post(Runnable {
            binding.refLayout.isRefreshing = true
            viewModel.getUsers()
        })

        adapter = AYuuAdapter()
        binding.rcyUsers.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcyUsers.adapter = adapter
    }

    private fun handleObservers() {
        viewModel.isLoading.observe(this){isLoading->
            binding.refLayout.isRefreshing = isLoading
            if(isLoading){
                binding.rcyUsers.visibility = View.GONE
                binding.tvError.visibility = View.GONE
            }
        }
        viewModel.users.observe(this){ users ->
            if(users.isNotEmpty()){
                Log.i("Users", users.toString())
                binding.rcyUsers.visibility = View.VISIBLE
                binding.tvError.visibility = View.GONE
                adapter.submitList(users)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRefresh() {
        viewModel.getUsers()
    }
}