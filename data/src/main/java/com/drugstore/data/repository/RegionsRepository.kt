package com.drugstore.data.repository

import androidx.lifecycle.MutableLiveData
import com.drugstore.data.database.dao.RegionsDao
import com.drugstore.data.repository.core.*
import com.drugstore.data.webservice.front.FrontService
import com.drugstore.domain.entity.Region
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import org.threeten.bp.Duration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegionsRepository @Inject constructor(
    private val cacheRecordsRepository: CacheRecordsRepository,
    private val regionsDao: RegionsDao,
    private val frontService: FrontService
) {

    fun getRegions(): Resource<List<Region>> {
        val fetchState = MutableLiveData<JobState?>(
            Completed
        )

        val refresh = suspend {
            try {
                fetchState.value = Active
                refreshRegions()
                fetchState.value = Completed
            } catch (e: Exception) {
                fetchState.value =
                    Cancelled(e)
                throw e
            }
        }

        val update = suspend {
            if (cacheRecordsRepository.isCacheExpired(KEY_REGIONS)) refresh()
        }

        return Resource(
            regionsDao.getAll(),
            fetchState,
            refresh,
            update
        )
    }

    internal suspend fun refreshRegions() = withContext(Default) {
        val response =
            call { frontService.getLocaleIndex() }
        val regions = response.map { it.map() }
        regionsDao.deleteAndInsert(regions)
        cacheRecordsRepository.setCacheExpiration(
            KEY_REGIONS,
            LIFESPAN_REGIONS
        )
    }

    internal suspend fun getCountries() = regionsDao.getCountriesSuspend()

    fun getName(id: String) = regionsDao.getName(id)

    fun getCountry(id: String) = regionsDao.getCountry(id)

    companion object {
        const val KEY_REGIONS = "regions"
        private val LIFESPAN_REGIONS: Duration = Duration.ofHours(2)
    }
}