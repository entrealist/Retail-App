package com.drugstore.data.di

import android.app.Application
import androidx.room.Room
import com.drugstore.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun appDatabase(application: Application) =
        Room.databaseBuilder(application, AppDatabase::class.java, "drugstore.db")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

    @Provides
    @Singleton
    fun addressesDao(database: AppDatabase) = database.addressesDao()

    @Provides
    @Singleton
    fun biometricCredentialsDao(database: AppDatabase) = database.biometricCredentialsDao()

    @Provides
    @Singleton
    fun cacheRecordsDao(database: AppDatabase) = database.cacheRecordsDao()

    @Provides
    @Singleton
    fun categoriesDao(database: AppDatabase) = database.categoriesDao()

    @Provides
    @Singleton
    fun contactDetailsDao(database: AppDatabase) = database.contactDetailsDao()

    @Provides
    @Singleton
    fun generalInfoDao(database: AppDatabase) = database.generalInfoDao()

    @Provides
    @Singleton
    fun ordersDao(database: AppDatabase) = database.ordersDao()

    @Provides
    @Singleton
    fun productsDao(database: AppDatabase) = database.productsDao()

    @Provides
    @Singleton
    fun regionsDao(database: AppDatabase) = database.regionsDao()

    @Provides
    @Singleton
    fun sessionDao(database: AppDatabase) = database.sessionDao()

    @Provides
    @Singleton
    fun userDao(database: AppDatabase) = database.userDao()
}