package com.phamthang.base.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T> : ViewModel() {

    private val _viewState = MutableLiveData<BaseViewState<T>>(BaseViewState.Loading)
    val viewState: LiveData<BaseViewState<T>> get() = _viewState

    protected fun setLoading() {
        _viewState.postValue(BaseViewState.Loading)
    }

    protected fun setSuccess(data: T) {
        _viewState.postValue(BaseViewState.Success(data))
    }

    protected fun setError(message: String) {
        _viewState.postValue(BaseViewState.Error(message))
    }
}



