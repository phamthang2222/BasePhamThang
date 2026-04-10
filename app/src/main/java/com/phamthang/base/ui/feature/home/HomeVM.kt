package com.phamthang.base.ui.feature.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phamthang.base.R
import com.phamthang.base.repository.WallpaperRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    @ApplicationContext context: Context,
    private val wallpaperRepository: WallpaperRepository
) : ViewModel() {
    private val _selectedNav = MutableLiveData(R.id.homeFragment)
    val selectedNav: LiveData<Int> = _selectedNav


    fun setNavigation(destination: Int) {
        if (_selectedNav.value != destination) {
            _selectedNav.value = destination
        }
    }
}