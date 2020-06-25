package com.drugstore.data.di

import com.drugstore.data.repository.SessionRepository
import com.drugstore.data.webservice.SessionHolder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
interface RepositoryModule {

    @Binds
    fun sessionHolder(sessionRepository: SessionRepository): SessionHolder
}