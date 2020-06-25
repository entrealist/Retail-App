package com.drugstore.data.repository.core

sealed class JobState

object Active : JobState()
object Completed : JobState()
data class Cancelled(val cause: Exception) : JobState()