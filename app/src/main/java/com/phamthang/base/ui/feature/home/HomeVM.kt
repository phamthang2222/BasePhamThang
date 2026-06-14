package com.phamthang.base.ui.feature.home

import androidx.lifecycle.viewModelScope
import com.phamthang.base.R
import com.phamthang.base.base.BaseViewModel
import com.phamthang.base.domain.usecase.GetWallpapersUseCase
import com.phamthang.base.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeVM @Inject constructor(
    private val getWallpapersUseCase: GetWallpapersUseCase
) : BaseViewModel<HomeState, HomeEvent>() {

    override fun initialState() = HomeState(selectedNav = R.id.homeFragment)

    init {
        loadWallpapers()
    }

    private fun loadWallpapers() {
        viewModelScope.launch {
            getWallpapersUseCase(Unit).collect { resource ->
                when (resource) {
                    is Resource.Loading -> updateState { copy(isLoading = true) }
                    is Resource.Success -> updateState { 
                        copy(isLoading = false, wallpapers = resource.data) 
                    }
                    is Resource.Error -> {
                        updateState { copy(isLoading = false, error = resource.message) }
                        sendEvent(HomeEvent.ShowToast(resource.message))
                    }
                }
            }
        }
    }

    fun setNavigation(destination: Int) {
        if (uiState.value.selectedNav != destination) {
            updateState { copy(selectedNav = destination) }
        }
    }
}
