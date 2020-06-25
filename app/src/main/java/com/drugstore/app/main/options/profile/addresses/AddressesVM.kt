package com.drugstore.app.main.options.profile.addresses

import android.app.Application
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.drugstore.R
import com.drugstore.app.core.handle
import com.drugstore.app.core.item.ActionItem
import com.drugstore.data.repository.AddressesRepository
import com.drugstore.data.repository.core.Active
import com.drugstore.data.repository.core.Cancelled
import com.drugstore.data.repository.core.Completed
import com.drugstore.domain.entity.Address
import com.kasprov.android.core.lifecycle.BaseViewModel
import com.kasprov.android.core.lifecycle.combine
import com.kasprov.android.core.lifecycle.set
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddressesVM @ViewModelInject internal constructor(
    application: Application,
    addressesRepository: AddressesRepository
) : BaseViewModel() {
    private val addressesResource = addressesRepository.getAddresses()
    val addresses = addressesResource.resource.asLiveData()
    private val addressesFetchState = addressesResource.fetchState

    private val addressesEmptyItem = ActionItem(
        application.getString(R.string.addresses_empty_addresses_title),
        application.getString(R.string.addresses_empty_addresses_action),
        View.OnClickListener { addAddress() }
    )

    private val addressesRetryItem = ActionItem(
        application.getString(R.string.addresses_retry_update_addresses_title),
        application.getString(R.string.addresses_retry_update_addresses_action),
        View.OnClickListener { refresh() }
    )

    val addressesFooterItems = addresses.combine(addressesFetchState) { addresses, fetchState ->
        when {
            fetchState is Completed && addresses.isEmpty() -> listOf(addressesEmptyItem)
            fetchState is Cancelled && addresses.isEmpty() -> listOf(addressesRetryItem)
            else -> null
        }
    }
    val isAddressesRefreshingIndicatorVisible = addressesFetchState.map { it is Active }
    val isAddAddressControlVisible: LiveData<Boolean?> = addresses.map { it.isNotEmpty() }

    private var updateAddressesJob: Job? = null

    internal fun onStart() {
        if (updateAddressesJob?.isCompleted == false) return
        updateAddressesJob = viewModelScope.launch {
            try {
                addressesResource.update()
            } catch (e: Exception) {
                e.handle(navigateTo)
            }
        }
    }

    internal fun refresh() {
        if (updateAddressesJob?.isCompleted == false) return
        updateAddressesJob = viewModelScope.launch {
            try {
                addressesResource.refresh()
            } catch (e: Exception) {
                e.handle(navigateTo)
            }
        }
    }

    fun addAddress() = navigateTo.set(AddressesFragmentDirections.actionAddressesToPersistAddress())

    fun edit(address: Address) = navigateTo.set(AddressesFragmentDirections.actionAddressesToPersistAddress(address.id))

//    TODO delete operation is hidden for now
//    fun delete(address: Address) {
//    }
}