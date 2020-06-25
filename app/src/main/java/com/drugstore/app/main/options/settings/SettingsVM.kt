package com.drugstore.app.main.options.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.drugstore.R
import com.drugstore.data.repository.BiometricRepository
import com.drugstore.data.repository.RegionsRepository
import com.drugstore.data.repository.SessionRepository
import com.kasprov.android.core.lifecycle.BaseViewModel
import com.kasprov.android.core.lifecycle.set
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class SettingsVM @ViewModelInject internal constructor(
    biometricRepository: BiometricRepository,
    regionsRepository: RegionsRepository,
    sessionRepository: SessionRepository
) : BaseViewModel() {
    val regionName = sessionRepository.regionId.filterNotNull().flatMapLatest {
        regionsRepository.getName(it)
    }.asLiveData()

    val biometricStatusResId = biometricRepository.isBiometricAuthenticationEnabled.map {
        if (it) R.string.settings_biometric_enabled else R.string.settings_biometric_disabled
    }.asLiveData()

    val isBiometricAvailable = MutableLiveData<Boolean?>()

    fun notifications() = navigateTo.set(SettingsFragmentDirections.actionSettingsToNotifications())

    fun region() = navigateTo.set(SettingsFragmentDirections.actionSettingsToRegion())

    fun biometric() = navigateTo.set(SettingsFragmentDirections.actionSettingsToBiometric())
}