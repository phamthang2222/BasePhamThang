package com.phamthang.base.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * BaseViewModel nâng cấp với StateFlow và SharedFlow.
 * - StateFlow: Dùng cho UI State
 * - SharedFlow: Dùng cho UI Event (dữ liệu một lần như Navigation, Toast).
 */
abstract class BaseViewModel<S, E> : ViewModel() {

    // Trạng thái của UI
    private val _uiState = MutableStateFlow<S>(initialState())
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    // Sự kiện xảy ra một lần (SingleLiveEvent replacement)
    private val _uiEvent = MutableSharedFlow<E>()
    val uiEvent: SharedFlow<E> = _uiEvent.asSharedFlow()

    abstract fun initialState(): S

    /**
     * Cập nhật UI State một cách an toàn.
     */
    protected fun updateState(reducer: S.() -> S) {
        _uiState.value = _uiState.value.reducer()
    }

    /**
     * Gửi một sự kiện tới UI.
     */
    protected fun sendEvent(event: E) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
