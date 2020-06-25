package com.drugstore.app.main.options.settings.region

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drugstore.app.setLanguageTag
import com.drugstore.data.repository.RegionsRepository
import com.drugstore.data.repository.SessionRepository
import com.drugstore.domain.entity.Region
import com.kasprov.android.core.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RegionVM @ViewModelInject internal constructor(
    @Assisted handle: SavedStateHandle,
    regionsRepository: RegionsRepository,
    private val sessionRepository: SessionRepository
) : BaseViewModel() {
    private val regionsResource = regionsRepository.getRegions()
    val regions = regionsResource.resource.asLiveData()
    private val currentRegionId = sessionRepository.regionId.asLiveData()

    private val _selectedRegionId = handle.getLiveData<String?>("selectedRegionId")
    val selectedRegionId: LiveData<String?> = _selectedRegionId

    val isSubmitButtonVisible: LiveData<Boolean?> = MediatorLiveData<Boolean?>().apply {
        addNonNullSource(selectedRegionId) { value = it != currentRegionId.value }
        addNonNullSource(currentRegionId) { value = it != selectedRegionId.value }
    }

    private val configurationChanged = MutableLiveData<Event<Any>?>()
    val onConfigurationChanged: LiveData<Event<Any>?> = configurationChanged

    private var submitJob: Job? = null

    init {
        _selectedRegionId.takeIf { it.value == null }?.apply {
            navigateTo.addOneTimeNonNullSource(currentRegionId) { value = it }
        }
        viewModelScope.launch {
            try {
                regionsResource.update()
            } catch (ignored: Exception) {
            }
        }
    }

    fun selectRegion(region: Region) {
        _selectedRegionId.value = region.id
    }

    fun submit(context: Context) {
        if (submitJob?.isCompleted == false) return

        val selectedRegionId = selectedRegionId.value ?: return
        if (selectedRegionId == currentRegionId.value) return

        submitJob = viewModelScope.launch {
            sessionRepository.setRegion(selectedRegionId)
            context.setLanguageTag(selectedRegionId)
            configurationChanged.call()
        }
    }
}