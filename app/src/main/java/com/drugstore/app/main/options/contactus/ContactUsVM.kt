package com.drugstore.app.main.options.contactus

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drugstore.R
import com.drugstore.app.core.findFirstContainedKey
import com.drugstore.app.core.handle
import com.drugstore.app.core.html
import com.drugstore.app.core.item.ActionItem
import com.drugstore.app.core.item.LoadingItem
import com.drugstore.data.repository.CommonRepository
import com.drugstore.data.repository.core.Active
import com.drugstore.data.repository.core.Cancelled
import com.drugstore.data.repository.entity.Input
import com.drugstore.domain.entity.ContactDetail
import com.kasprov.android.core.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactUsVM @ViewModelInject internal constructor(
    application: Application,
    @Assisted handle: SavedStateHandle,
    private val commonRepository: CommonRepository
) : BaseViewModel() {
    private val contactDetailsResource = commonRepository.getContactDetails()
    val contactDetails = contactDetailsResource.resource.map { contactDetails ->
        withContext(Dispatchers.Default) {
            contactDetails.map {
                ContactDetail(
                    it.id,
                    it.data.html().toString(),
                    it.type
                )
            }
        }
    }.asLiveData()
    private val contactDetailsFetchState = contactDetailsResource.fetchState

    private val contactDetailsRetryItem = ActionItem(
        application.getString(R.string.contact_us_retry_update_contact_details_title),
        application.getString(R.string.contact_us_retry_update_contact_details_action),
        View.OnClickListener { updateContactDetails() }
    )

    val contactDetailsFooterItems = contactDetails.combine(contactDetailsFetchState) { contactDetails, fetchState ->
        when {
            fetchState is Active && contactDetails.isEmpty() -> listOf(LoadingItem)
            fetchState is Cancelled && contactDetails.isEmpty() -> listOf(contactDetailsRetryItem)
            else -> null
        }
    }

    val email = MutableLiveData<String?>()
    val name = MutableLiveData<String?>()
    val details = MutableLiveData<String?>()

    private val _inputErrors = handle.getLiveData<MutableMap<Input, String>?>("input_errors")
    val inputErrors: LiveData<out Map<Input, String>?> = _inputErrors
    var inputs: Set<Input> = emptySet()

    private val focusInput = MutableLiveData<Event<Input>?>()
    val onFocusInput: LiveData<Event<Input>?> = focusInput

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading
    val areControlsEnabled: LiveData<Boolean?> = _isLoading.mapNonNull { !it }

    init {
        updateContactDetails()
    }

    private fun updateContactDetails() = viewModelScope.launch {
        try {
            contactDetailsResource.update()
        } catch (e: Exception) {
            e.handle(navigateTo)
        }
    }

    fun submit() {
        inputErrors.value?.findFirstContainedKey(inputs)?.let {
            focusInput.set(it)
            return
        }

        val email = email.value
        val name = name.value
        val details = details.value

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val message = commonRepository.contactUs(email, name, details)
                navigateTo.set(
                    ContactUsFragmentDirections.actionToMessage(
                        message = message,
                        buttonTextResId = R.string.contact_us_message_success_button,
                        requestKey = FragmentRequest.ACKNOWLEDGE_RESPONSE.key,
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
        FragmentRequest.ACKNOWLEDGE_RESPONSE -> {
            navigateTo.set(ContactUsFragmentDirections.actionContactUsDone())
        }
    }

    enum class FragmentRequest(val key: String) {
        ACKNOWLEDGE_RESPONSE("contact_us_acknowledge_response")
    }
}