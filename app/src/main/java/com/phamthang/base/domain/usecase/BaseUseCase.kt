package com.phamthang.base.domain.usecase

import com.phamthang.base.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

/**
 * BaseUseCase cho các tác vụ trả về Flow (Asynchronous streams).
 * Giúp chuẩn hóa cách thức gọi và xử lý dữ liệu ở tầng Domain.
 * P: Params, R: Return Type
 */
abstract class FlowUseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameters: P): Flow<Resource<R>> {
        return execute(parameters)
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown Error", e)) }
            .flowOn(dispatcher)
    }

    protected abstract suspend fun execute(parameters: P): Flow<Resource<R>>
}

/**
 * BaseUseCase cho các tác vụ Suspend (Single shot operations).
 */
abstract class SuspendUseCase<in P, out R>(private val dispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameters: P): Resource<R> {
        return try {
            kotlinx.coroutines.withContext(dispatcher) {
                execute(parameters)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown Error", e)
        }
    }

    protected abstract suspend fun execute(parameters: P): Resource<R>
}
