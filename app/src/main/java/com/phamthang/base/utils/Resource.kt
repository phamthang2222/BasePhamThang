package com.phamthang.base.utils

/**
 * Resource class dùng để wrap dữ liệu và trạng thái của nó (Success, Error, Loading).
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    /**
     * Helper functions để kiểm tra trạng thái nhanh hơn.
     */
    fun isSuccess() = this is Success
    fun isError() = this is Error
    fun isLoading() = this is Loading
}
