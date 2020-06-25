package com.drugstore.app.main.options

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.drugstore.CoreGraphDirections
import com.drugstore.app.core.handle
import com.drugstore.data.repository.AuthRepository
import com.drugstore.data.repository.CommonRepository
import com.kasprov.android.core.lifecycle.BaseViewModel
import com.kasprov.android.core.lifecycle.set
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OptionsVM @ViewModelInject internal constructor(
    private val authRepository: AuthRepository,
    private val commonRepository: CommonRepository
) : BaseViewModel() {

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading

    private var getRequestJob: Job? = null

    fun profile() = navigateTo.set(OptionsFragmentDirections.actionOptionsToProfile())

    fun settings() = navigateTo.set(OptionsFragmentDirections.actionOptionsToSettings())

    fun termsAndConditions() {
        if (getRequestJob?.isCompleted == false) return
        getRequestJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                commonRepository.getTermsAndConditionsRequest()?.let {
                    navigateTo.set(CoreGraphDirections.actionToWebView(it))
                }
            } catch (e: Exception) {
                e.handle(navigateTo)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun privacyPolicy() {
        if (getRequestJob?.isCompleted == false) return
        getRequestJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                commonRepository.getPrivacyPolicyRequest()?.let {
                    navigateTo.set(CoreGraphDirections.actionToWebView(it))
                }
            } catch (e: Exception) {
                e.handle(navigateTo)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun contactUs() = navigateTo.set(OptionsFragmentDirections.actionOptionsToContactUs())

    fun logout() {
        if (getRequestJob?.isCompleted == false) return
        getRequestJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                authRepository.logout()
            } catch (e: Exception) {
                _isLoading.value = false
                e.handle(navigateTo)
            }
        }
    }
}