package com.phamthang.reverseaudio.base

sealed class BaseViewState<out T> {
    object Loading : BaseViewState<Nothing>()
    data class Success<T>(val data: T) : BaseViewState<T>()
    data class Error(val message: String) : BaseViewState<Nothing>()
}