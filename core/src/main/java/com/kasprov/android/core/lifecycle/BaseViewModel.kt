package com.kasprov.android.core.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections

open class BaseViewModel : ViewModel() {

    protected val navigateTo = MediatorLiveData<Event<NavDirections>>()
    val onNavigateTo: LiveData<Event<NavDirections>?> = navigateTo

    protected val navigateUp = MediatorLiveData<Event<Any>>()
    val onNavigateUp: LiveData<Event<Any>?> = navigateUp
}