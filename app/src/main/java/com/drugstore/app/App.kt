package com.drugstore.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.drugstore.data.repository.SessionRepository
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), DefaultLifecycleObserver {

    @Inject lateinit var sessionRepository: SessionRepository

    override fun attachBaseContext(base: Context) =
        super.attachBaseContext(base.createConfigurationContext())

    override fun onCreate() {
        updateConfiguration()
        super<Application>.onCreate()
        AndroidThreeTen.init(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStop(owner: LifecycleOwner) {
        sessionRepository.onApplicationStopped()
    }
}