package com.phamthang.base.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<T> : ViewModel() {

    private val _viewState = MutableStateFlow<BaseViewState<T>>(BaseViewState.Idle)
    val viewState: StateFlow<BaseViewState<T>> get() = _viewState

    protected fun setLoading() {
        _viewState.value = BaseViewState.Loading
    }

    protected fun setSuccess(data: T) {
        _viewState.value = BaseViewState.Success(data)
    }

    protected fun setError(message: String) {
        _viewState.value = BaseViewState.Error(message)
    }

    protected fun setIdle() {
        _viewState.value = BaseViewState.Idle
    }

}



