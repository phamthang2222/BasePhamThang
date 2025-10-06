package com.phamthang.reverseaudio.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phamthang.reverseaudio.R

class HomeVM(application: Application) : AndroidViewModel(application) {
    private val _selectedNav = MutableLiveData(R.id.homeFragment)
    val selectedNav: LiveData<Int> = _selectedNav


    fun setNavigation(destination: Int) {
        if (_selectedNav.value != destination) {
            _selectedNav.value = destination
        }
    }
}