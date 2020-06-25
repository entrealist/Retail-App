package com.drugstore.app.login.login

import androidx.biometric.BiometricPrompt
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drugstore.app.core.findFirstContainedKey
import com.drugstore.app.core.handle
import com.drugstore.data.repository.AuthRepository
import com.drugstore.data.repository.BiometricRepository
import com.drugstore.data.repository.SessionRepository
import com.drugstore.data.repository.entity.Input
import com.drugstore.data.repository.entity.Input.PASSWORD
import com.kasprov.android.core.lifecycle.*
import kotlinx.coroutines.launch

class LoginVM @ViewModelInject internal constructor(
    @Assisted handle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val biometricRepository: BiometricRepository,
    sessionRepository: SessionRepository
) : BaseViewModel() {
    val email = MutableLiveData<String?>()
    val password = MutableLiveData<String?>()

    val isBiometricAuthenticationAvailable =
        biometricRepository.isBiometricAuthenticationAvailableFor(email.asFlow()).asLiveData()

    private val _inputErrors = handle.getLiveData<MutableMap<Input, String>?>("input_errors")
    val inputErrors: LiveData<out Map<Input, String>?> = _inputErrors
    var inputs: Set<Input> = emptySet()

    private val authenticateBiometric = MutableLiveData<Event<BiometricPrompt.CryptoObject>?>()
    val onAuthenticateBiometric: LiveData<Event<BiometricPrompt.CryptoObject>?> = authenticateBiometric

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading
    val areControlsEnabled: LiveData<Boolean?> = _isLoading.mapNonNull { !it }

    init {
        navigateTo.addOneTimeSource(sessionRepository.regionId.asLiveData()) {
            if (it == null) navigateTo.set(LoginFragmentDirections.actionLoginToSelectRegion())
        }
    }

    fun recoverPassword() = navigateTo.set(LoginFragmentDirections.actionLoginToRecoverPassword())

    fun register() = navigateTo.set(LoginFragmentDirections.actionLoginToRegister())

    fun submit() {
        inputErrors.value?.findFirstContainedKey(inputs)?.let { return }

        val email = email.value
        val password = password.value

        viewModelScope.launch {
            try {
                _isLoading.value = true
                authRepository.login(email, password)
            } catch (e: Exception) {
                _isLoading.value = false
                e.handle(navigateTo) { _inputErrors.value = errors?.toMutableMap() }
            }
        }
    }

    fun setInputError(input: Input, value: String) {
        _inputErrors.value = (_inputErrors.value ?: mutableMapOf()).apply { put(input, value) }
    }

    fun clearInputError(input: Input) = _inputErrors.value?.takeIf { it.containsKey(input) }?.let {
        _inputErrors.value = it.apply { remove(input) }
    }

    fun authenticateBiometric() = viewModelScope.launch {
        clearInputError(PASSWORD)
        val cipher = biometricRepository.getCipher() ?: return@launch
        authenticateBiometric.set(BiometricPrompt.CryptoObject(cipher))
    }

    internal fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = viewModelScope.launch {
        val cipher = result.cryptoObject?.cipher ?: return@launch
        password.value = biometricRepository.getPassword(cipher)
        submit()
    }
}