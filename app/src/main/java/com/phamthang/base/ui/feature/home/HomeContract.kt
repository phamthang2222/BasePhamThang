package com.phamthang.base.ui.feature.home

import com.phamthang.base.domain.model.WallpaperDomain

/**
 * Định nghĩa UI State và UI Event cho màn hình Home.
 * Giúp quản lý trạng thái màn hình một cách tập trung.
 */
data class HomeState(
    val isLoading: Boolean = false,
    val wallpapers: List<WallpaperDomain> = emptyList(),
    val selectedNav: Int = 0,
    val error: String? = null
)

sealed class HomeEvent {
    data class ShowToast(val message: String) : HomeEvent()
    object NavigateToSettings : HomeEvent()
}
