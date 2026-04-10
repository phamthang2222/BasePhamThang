package com.phamthang.base.base

sealed class BaseViewState<out T> {
    object Idle : BaseViewState<Nothing>()

    object Loading : BaseViewState<Nothing>()
    data class Success<T>(val data: T) : BaseViewState<T>()
    data class Error(val message: String) : BaseViewState<Nothing>()
}