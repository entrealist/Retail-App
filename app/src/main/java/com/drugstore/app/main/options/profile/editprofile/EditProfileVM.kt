package com.drugstore.app.main.options.profile.editprofile

import android.os.Bundle
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drugstore.R
import com.drugstore.app.core.findFirstContainedKey
import com.drugstore.app.core.handle
import com.drugstore.data.repository.CustomerRepository
import com.drugstore.data.repository.UserRepository
import com.drugstore.data.repository.entity.Input
import com.kasprov.android.core.lifecycle.*
import kotlinx.coroutines.launch

class EditProfileVM @ViewModelInject internal constructor(
    @Assisted handle: SavedStateHandle,
    private val customerRepository: CustomerRepository,
    userRepository: UserRepository
) : BaseViewModel() {
    val user = userRepository.user.asLiveData()

    val email = MediatorLiveData<String?>().apply {
        addOneTimeNonNullSource(user) { if (value == null) value = it.email }
    }

    private val _inputErrors = handle.getLiveData<MutableMap<Input, String>?>("input_errors")
    val inputErrors: LiveData<out Map<Input, String>?> = _inputErrors
    var inputs: Set<Input> = emptySet()

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading
    val areControlsEnabled: LiveData<Boolean?> = _isLoading.mapNonNull { !it }

    fun submit() {
        inputErrors.value?.findFirstContainedKey(inputs)?.let { return }

        val email = email.value
        if (email == user.value?.email) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val message = customerRepository.updateProfile(email)
                navigateTo.set(
                    EditProfileFragmentDirections.actionToMessage(
                        message = message,
                        buttonTextResId = R.string.edit_profile_message_success_button,
                        requestKey = FragmentRequest.ACKNOWLEDGE_RESPONSE.key,
                        cancelable = false
                    )
                )
            } catch (e: Exception) {
                e.handle(navigateTo) { _inputErrors.value = errors?.toMutableMap() }
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
            navigateTo.set(EditProfileFragmentDirections.actionEditProfileDone())
        }
    }

    fun cancel() = navigateUp.call()

    enum class FragmentRequest(val key: String) {
        ACKNOWLEDGE_RESPONSE("edit_profile_acknowledge_response")
    }
}