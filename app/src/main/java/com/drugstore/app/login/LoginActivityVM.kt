package com.drugstore.app.login

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.drugstore.data.repository.SessionRepository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take

class LoginActivityVM @ViewModelInject internal constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val navigateToMain = sessionRepository.isUserAuthenticated
        .filter { it }
        .take(1)
        .asLiveData().map { }

    fun shouldNavigateToMain() = sessionRepository.isUserAuthenticatedBlocking
}