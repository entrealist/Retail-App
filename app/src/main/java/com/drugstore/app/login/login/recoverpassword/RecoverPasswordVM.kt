package com.drugstore.app.login.login.recoverpassword

import android.os.Bundle
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.drugstore.R
import com.drugstore.app.core.findFirstContainedKey
import com.drugstore.app.core.handle
import com.drugstore.data.repository.AuthRepository
import com.drugstore.data.repository.entity.Input
import com.kasprov.android.core.lifecycle.BaseViewModel
import com.kasprov.android.core.lifecycle.call
import com.kasprov.android.core.lifecycle.mapNonNull
import com.kasprov.android.core.lifecycle.set
import kotlinx.coroutines.launch

class RecoverPasswordVM @ViewModelInject internal constructor(
    @Assisted handle: SavedStateHandle,
    private val authRepository: AuthRepository
) : BaseViewModel() {
    val email = MutableLiveData<String?>()

    private val _inputErrors = handle.getLiveData<MutableMap<Input, String>?>("input_errors")
    val inputErrors: LiveData<out Map<Input, String>?> = _inputErrors
    var inputs: Set<Input> = emptySet()

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading
    val areControlsEnabled: LiveData<Boolean?> = _isLoading.mapNonNull { !it }

    fun submit() {
        inputErrors.value?.findFirstContainedKey(inputs)?.let { return }

        val email = email.value

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val message = authRepository.recoverPassword(email)
                navigateTo.set(RecoverPasswordFragmentDirections.actionToMessage(
                    titleResId = R.string.recover_password_message_success_title,
                    message = message,
                    buttonTextResId = R.string.recover_password_message_success_button,
                    requestKey = FragmentRequest.ACKNOWLEDGE_RESPONSE.key,
                    cancelable = false
                ))
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
            navigateTo.set(RecoverPasswordFragmentDirections.actionRecoverPasswordDone())
        }
    }

    fun cancel() = navigateUp.call()

    enum class FragmentRequest(val key: String) {
        ACKNOWLEDGE_RESPONSE("recover_password_acknowledge_response")
    }
}