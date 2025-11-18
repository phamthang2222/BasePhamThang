package com.phamthang.base.ui.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phamthang.base.R

class HomeVM(application: Application) : AndroidViewModel(application) {
    private val _selectedNav = MutableLiveData(R.id.homeFragment)
    val selectedNav: LiveData<Int> = _selectedNav


    fun setNavigation(destination: Int) {
        if (_selectedNav.value != destination) {
            _selectedNav.value = destination
        }
    }
}