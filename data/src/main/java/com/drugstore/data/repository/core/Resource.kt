package com.drugstore.data.repository.core

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

data class Resource<T>(
    val resource: Flow<T>,
    val fetchState: LiveData<JobState?>,
    val refresh: suspend () -> Unit,
    val update: suspend () -> Unit
)