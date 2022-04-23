package com.example.explorer

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class BaseFragment : Fragment() {

    fun <T : ViewModel> createViewModel(modelClass: Class<T>): T {
        val app = requireActivity().application
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app)
        return ViewModelProvider(viewModelStore, factory).get(modelClass)
    }

    open fun onBackPressed() = true
}