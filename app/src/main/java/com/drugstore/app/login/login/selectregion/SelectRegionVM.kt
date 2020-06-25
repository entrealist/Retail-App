package com.drugstore.app.login.login.selectregion

import android.app.Application
import android.content.Context
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.drugstore.R
import com.drugstore.app.core.handle
import com.drugstore.app.core.item.ActionItem
import com.drugstore.app.core.item.LoadingItem
import com.drugstore.app.setLanguageTag
import com.drugstore.data.repository.RegionsRepository
import com.drugstore.data.repository.SessionRepository
import com.drugstore.data.repository.core.Active
import com.drugstore.data.repository.core.Cancelled
import com.drugstore.domain.entity.Region
import com.kasprov.android.core.lifecycle.BaseViewModel
import com.kasprov.android.core.lifecycle.Event
import com.kasprov.android.core.lifecycle.call
import com.kasprov.android.core.lifecycle.combine
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SelectRegionVM @ViewModelInject internal constructor(
    application: Application,
    regionsRepository: RegionsRepository,
    private val sessionRepository: SessionRepository
) : BaseViewModel() {
    private val regionsResource = regionsRepository.getRegions()
    val regions = regionsResource.resource.asLiveData()
    private val regionsFetchState = regionsResource.fetchState

    private val regionsRetryItem = ActionItem(
        application.getString(R.string.select_region_retry_update_regions_title),
        application.getString(R.string.select_region_retry_update_regions_action),
        View.OnClickListener { updateRegions() }
    )

    val regionsFooterItems = regions.combine(regionsFetchState) { regions, fetchState ->
        when {
            fetchState is Active && regions.isEmpty() -> listOf(LoadingItem)
            fetchState is Cancelled && regions.isEmpty() -> listOf(regionsRetryItem)
            else -> null
        }
    }

    private val configurationChanged = MutableLiveData<Event<Any>?>()
    val onConfigurationChanged: LiveData<Event<Any>?> = configurationChanged

    private var submitJob: Job? = null

    init {
        updateRegions()
    }

    private fun updateRegions() = viewModelScope.launch {
        try {
            regionsResource.update()
        } catch (e: Exception) {
            e.handle(navigateTo)
        }
    }

    fun selectRegion(context: Context, region: Region) {
        if (submitJob?.isCompleted == false) return
        submitJob = viewModelScope.launch {
            sessionRepository.setRegion(region.id)
            context.setLanguageTag(region.id)
            configurationChanged.call()
            navigateUp.call()
        }
    }
}