package com.phamthang.base.extention

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.phamthang.base.base.BaseActivity
import com.phamthang.base.base.BaseViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

fun <T> Fragment.collectLifecycleFlow(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(state) {
            flow.collectLatest {
                action(it)
            }
        }
    }
}

fun <T> ComponentActivity.collectLifecycleFlow(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state) {
            flow.collectLatest {
                action(it)
            }
        }
    }
}

fun <T> Fragment.observeViewState(
    flow: Flow<BaseViewState<T>>,
    onLoading: (() -> Unit)? = null,
    onSuccess: (T) -> Unit,
    onError: ((String) -> Unit)? = null
) {
    collectLifecycleFlow(flow) { state ->
        when (state) {
            is BaseViewState.Loading -> {
                if (onLoading != null) {
                    onLoading.invoke()
                } else {
                    (activity as? BaseActivity<*, *>)?.showLoading()
                }
            }

            is BaseViewState.Success -> {
                (activity as? BaseActivity<*, *>)?.hideLoading()
                onSuccess(state.data)
            }

            is BaseViewState.Error -> {
                (activity as? BaseActivity<*, *>)?.hideLoading()
                onError?.invoke(state.message)
            }
            else -> {}
        }
    }
}