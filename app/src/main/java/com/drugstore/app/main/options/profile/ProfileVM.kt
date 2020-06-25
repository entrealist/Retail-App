package com.drugstore.app.main.options.profile

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.drugstore.CoreGraphDirections
import com.drugstore.app.core.handle
import com.drugstore.data.repository.CommonRepository
import com.drugstore.data.repository.CustomerRepository
import com.drugstore.data.repository.UserRepository
import com.kasprov.android.core.lifecycle.BaseViewModel
import com.kasprov.android.core.lifecycle.set
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileVM @ViewModelInject internal constructor(
    private val commonRepository: CommonRepository,
    private val customerRepository: CustomerRepository,
    userRepository: UserRepository
) : BaseViewModel() {

    val user = userRepository.user.asLiveData()

    private val _isLoading = MutableLiveData<Boolean?>(false)
    val isLoadingIndicatorVisible: LiveData<Boolean?> = _isLoading

    private var updateProfileJob: Job? = null
    private var getRequestJob: Job? = null

    fun addresses() = navigateTo.set(ProfileFragmentDirections.actionProfileToAddresses())

    fun changePassword() = navigateTo.set(ProfileFragmentDirections.actionProfileToChangePassword())

    internal fun onStart() {
        if (updateProfileJob?.isCompleted == false) return
        updateProfileJob = viewModelScope.launch {
            try {
                customerRepository.updateProfile()
            } catch (ignored: Exception) {
            }
        }
    }

    fun medicalRecord() {
        if (getRequestJob?.isCompleted == false) return
        getRequestJob = viewModelScope.launch {
            try {
                _isLoading.value = true
                commonRepository.getMedicalRecordRequest()?.let {
                    navigateTo.set(CoreGraphDirections.actionToWebView(it))
                }
            } catch (e: Exception) {
                e.handle(navigateTo)
            } finally {
                _isLoading.value = false
            }
        }
    }
}