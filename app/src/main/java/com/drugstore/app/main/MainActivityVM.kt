package com.drugstore.app.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.drugstore.data.repository.CustomerRepository
import com.drugstore.data.repository.SessionRepository
import com.kasprov.android.core.lifecycle.Event
import com.kasprov.android.core.lifecycle.call
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class MainActivityVM @ViewModelInject internal constructor(
    private val customerRepository: CustomerRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val navigateToLogin = sessionRepository.isUserAuthenticated
        .filter { !it }
        .take(1)
        .asLiveData().map { }

    private val regionChanged = MutableLiveData<Event<Any>?>()
    val onRegionChanged: LiveData<Event<Any>?> = regionChanged

    private var updateProfileJob: Job? = null

    fun shouldNavigateToLogin() = !sessionRepository.isUserAuthenticatedBlocking

    fun onStart() {
        if (updateProfileJob?.isCompleted == false) return
        updateProfileJob = viewModelScope.launch {
            try {
                customerRepository.updateProfile()
            } catch (ignored: Exception) {
            }
        }
    }

    fun regionChanged() = regionChanged.call()
}