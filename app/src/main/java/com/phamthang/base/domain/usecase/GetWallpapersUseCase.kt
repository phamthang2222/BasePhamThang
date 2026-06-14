package com.phamthang.base.domain.usecase

import com.phamthang.base.di.IoDispatcher
import com.phamthang.base.domain.model.WallpaperDomain
import com.phamthang.base.domain.repository.WallpaperRepository
import com.phamthang.base.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase lấy danh sách hình nền.
 * Tách biệt logic này giúp ViewModel gọn nhẹ và dễ Unit Test.
 */
class GetWallpapersUseCase @Inject constructor(
    private val repository: WallpaperRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<Unit, List<WallpaperDomain>>(dispatcher) {

    override suspend fun execute(parameters: Unit): Flow<Resource<List<WallpaperDomain>>> {
        return repository.getAllWallpapers()
    }
}
