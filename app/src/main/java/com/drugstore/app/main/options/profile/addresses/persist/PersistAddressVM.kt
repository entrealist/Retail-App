package com.drugstore.app.main.options.profile.addresses.persist

import android.content.Context
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drugstore.R
import com.drugstore.app.core.addressTypeName
import com.drugstore.app.core.dialog.PickItemDialogFragment
import com.drugstore.app.core.findFirstContainedKey
import com.drugstore.app.core.handle
import com.drugstore.data.repository.AddressesRepository
import com.drugstore.data.repository.RegionsRepository
import com.drugstore.data.repository.SessionRepository
import com.drugstore.data.repository.UserRepository
import com.drugstore.data.repository.entity.Input
import com.drugstore.domain.entity.Address
import com.drugstore.domain.entity.Address.Type.DHL_PACKSTATION
import com.drugstore.domain.entity.Address.Type.REGULAR
import com.drugstore.domain.entity.Region
import com.kasprov.android.core.lifecycle.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class PersistAddressVM @ViewModelInject internal constructor(
    @Assisted handle: SavedStateHandle,
    private val addressesRepository: AddressesRepository,
    regionsRepository: RegionsRepository,
    sessionRepository: SessionRepository,
    userRepository: UserRepository
) : BaseViewModel() {
    private val addressId: Int = handle["addressId"] ?: NO_ID
    internal val isEditing = addressId != NO_ID
    private val address =
        if (isEditing) addressesRepository.getAddress(addressId).asLiveData() else MutableLiveData()

    private val addressTypes = MutableLiveData<List<Address.Type>?>()
    val addressTypeVisibility = addressTypes.mapNonNull { if (it.size > 1) VISIBLE else GONE }

    private val _addressType = handle.getLiveData<Address.Type?>("addressType")
    val addressType: LiveData<Address.Type?> = _addressType

    val inputsForRegularVisibility = addressType.mapNonNull { if (it == REGULAR) VISIBLE else GONE }
    val inputsForDhlPackstationVisibility = addressType.mapNonNull { if (it == DHL_PACKSTATION) VISIBLE else GONE }

    val title = MutableLiveData<String?>()
    val firstName = MutableLiveData<String?>()
    val lastName = MutableLiveData<String?>()
    val country = MutableLiveData<Region.Country?>()
    val postalCode = MutableLiveData<String?>()
    val city = MutableLiveData<String?>()
    val street = MutableLiveData<String?>()
    val houseNumber = MutableLiveData<String?>()
    val companyName = MutableLiveData<String?>()
    val packstationAddress = MutableLiveData<String?>()
    val packstationNumber = MutableLiveData<String?>()
    val phoneNumber = MutableLiveData<String?>()

    private val _inputErrors = handle.getLiveData<MutableMap<Input, String>?>("input_errors")
    val inputErrors: LiveData<out Map<Input, String>?> = _inputErrors
    var inputs: Set<Input> = emptySet()

    private val focusInput = MutableLiveData<Event<Input>?>()
    val onFocusInput: LiveData<Event<Input>?> = focusInput

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading
    val areControlsEnabled: LiveData<Boolean?> = _isLoading.mapNonNull { !it }

    init {
        if (isEditing) {
            navigateTo.addOneTimeNonNullSource(address) {
                addressTypes.value = addressesRepository.getCountryAddressTypes(it.country.id)
                _addressType.apply { if (value == null) value = it.type }
                title.apply { if (value == null) value = it.title }
                firstName.apply { if (value == null) value = it.firstName }
                lastName.apply { if (value == null) value = it.lastName }
                country.apply { value = it.country }
                postalCode.apply { if (value == null) value = it.postalCode }
                city.apply { if (value == null) value = it.city }
                street.apply { if (value == null) value = it.street }
                houseNumber.apply { if (value == null) value = it.houseNumber }
                companyName.apply { if (value == null) value = it.companyName }
                packstationAddress.apply { if (value == null) value = it.packstationAddress }
                packstationNumber.apply { if (value == null) value = it.packstationNumber }
                phoneNumber.apply { if (value == null) value = it.phoneNumber }
            }
        } else {
            val sessionRegionCountry = sessionRepository.regionId.filterNotNull().flatMapLatest {
                regionsRepository.getCountry(it)
            }.asLiveData()

            val sessionRegionAddressTypes = sessionRegionCountry.mapNonNull { addressesRepository.getCountryAddressTypes(it.id) }
            navigateTo.addOneTimeNonNullSource(sessionRegionAddressTypes) { addressTypes.value = it }

            _addressType.apply { if (value == null) value = REGULAR }
            navigateTo.addOneTimeNonNullSource(userRepository.user.asLiveData()) {
                firstName.apply { if (value == null) value = it.firstName }
                lastName.apply { if (value == null) value = it.lastName }
            }

            navigateTo.addOneTimeNonNullSource(sessionRegionCountry) { country.value = it }
        }
    }

    fun pickAddressType(context: Context) {
        val availableAddressTypes = addressTypes.value?.toTypedArray() ?: return
        val ids = availableAddressTypes.map { it.id }.toIntArray()
        val checkedItemId = (_addressType.value ?: availableAddressTypes[0]).id
        val labels = availableAddressTypes.mapNotNull { context.addressTypeName(it) }.toTypedArray()
        navigateTo.set(
            PersistAddressFragmentDirections.actionToPickItem(
                ids,
                checkedItemId,
                labels,
                FragmentRequest.PICK_ADDRESS_TYPE.key
            )
        )
    }

    fun submit() {
        inputErrors.value?.findFirstContainedKey(inputs)?.let {
            focusInput.set(it)
            return
        }

        val addressType = addressType.value ?: return
        val title = title.value
        val firstName = firstName.value
        val lastName = lastName.value
        val country = country.value
        val postalCode = postalCode.value
        val city = city.value
        val street = street.value
        val houseNumber = houseNumber.value
        val companyName = companyName.value
        val packstationAddress = packstationAddress.value
        val packstationNumber = packstationNumber.value
        val phoneNumber = phoneNumber.value

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val message = addressesRepository.updateAddress(
                    addressType, title, firstName, lastName, country, postalCode, city, street,
                    houseNumber, companyName, packstationAddress, packstationNumber, phoneNumber
                )
                navigateTo.set(
                    PersistAddressFragmentDirections.actionToMessage(
                        message = message,
                        buttonTextResId = R.string.persist_address_message_success_button,
                        requestKey = FragmentRequest.ACKNOWLEDGE_PERSIST_RESPONSE.key,
                        cancelable = false
                    )
                )
            } catch (e: Exception) {
                e.handle(navigateTo) {
                    _inputErrors.value = errors?.toMutableMap()
                    errors?.findFirstContainedKey(inputs)?.let { focusInput.set(it) }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearInputError(input: Input) = _inputErrors.value?.takeIf { it.containsKey(input) }?.let {
        _inputErrors.value = it.apply { remove(input) }
    }

    internal fun onFragmentResult(request: FragmentRequest, result: Bundle) = when (request) {
        FragmentRequest.PICK_ADDRESS_TYPE -> {
            val pickedItemId = PickItemDialogFragment.unpackPickedItemId(result)
            _addressType.value = Address.Type.getById(pickedItemId)
        }
        FragmentRequest.ACKNOWLEDGE_PERSIST_RESPONSE -> {
            navigateTo.set(PersistAddressFragmentDirections.actionPersistAddressDone())
        }
    }

    fun cancel() = navigateUp.call()

    enum class FragmentRequest(val key: String) {
        PICK_ADDRESS_TYPE("persist_address_pick_address_type"),
        ACKNOWLEDGE_PERSIST_RESPONSE("persist_address_acknowledge_persist_response")
    }

    companion object {
        private const val NO_ID = 0
    }
}