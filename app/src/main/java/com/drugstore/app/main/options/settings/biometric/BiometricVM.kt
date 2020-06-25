package com.drugstore.app.main.options.settings.biometric

import androidx.biometric.BiometricPrompt
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drugstore.R
import com.drugstore.app.core.findFirstContainedKey
import com.drugstore.app.core.handle
import com.drugstore.data.repository.BiometricRepository
import com.drugstore.data.repository.entity.Input
import com.kasprov.android.core.lifecycle.BaseViewModel
import com.kasprov.android.core.lifecycle.Event
import com.kasprov.android.core.lifecycle.mapNonNull
import com.kasprov.android.core.lifecycle.set
import kotlinx.coroutines.launch

class BiometricVM @ViewModelInject internal constructor(
    @Assisted handle: SavedStateHandle,
    private val biometricRepository: BiometricRepository
) : BaseViewModel() {
    val password = MutableLiveData<String?>()

    val isBiometricAuthenticationEnabled = biometricRepository.isBiometricAuthenticationEnabled.asLiveData()
    val biometricAuthenticationError = handle.getLiveData<CharSequence?>("biometricAuthenticationError")

    val titleResId = isBiometricAuthenticationEnabled.mapNonNull {
        if (it) R.string.biometric_title_enabled else R.string.biometric_title_disabled
    }

    private val _inputErrors = handle.getLiveData<MutableMap<Input, String>?>("input_errors")
    val inputErrors: LiveData<out Map<Input, String>?> = _inputErrors
    var inputs: Set<Input> = emptySet()

    private val authenticateBiometric = MutableLiveData<Event<BiometricPrompt.CryptoObject>?>()
    val onAuthenticateBiometric: LiveData<Event<BiometricPrompt.CryptoObject>?> = authenticateBiometric

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading
    val areControlsEnabled: LiveData<Boolean?> = _isLoading.mapNonNull { !it }

    fun enable() {
        inputErrors.value?.findFirstContainedKey(inputs)?.let { return }

        val password = password.value

        viewModelScope.launch {
            try {
                _isLoading.value = true
                biometricAuthenticationError.value = null
                biometricRepository.checkPassword(password)
                val cipher = biometricRepository.getCipher() ?: return@launch
                authenticateBiometric.set(BiometricPrompt.CryptoObject(cipher))
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

    internal fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = viewModelScope.launch {
        val password = password.value ?: return@launch
        val cipher = result.cryptoObject?.cipher ?: return@launch
        biometricRepository.enableBiometricAuthentication(password, cipher)
    }

    fun disable() = viewModelScope.launch { biometricRepository.disableBiometricAuthentication() }
}