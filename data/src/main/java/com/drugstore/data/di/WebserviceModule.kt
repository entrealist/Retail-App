package com.drugstore.data.di

import android.app.Application
import com.drugstore.data.BuildConfig
import com.drugstore.data.webservice.AuthHandler
import com.drugstore.data.webservice.ErrorHandler
import com.drugstore.data.webservice.HeaderInterceptor
import com.drugstore.data.webservice.auth.AuthService
import com.drugstore.data.webservice.auth.AuthServiceProxy
import com.drugstore.data.webservice.auth.PutLoginService
import com.drugstore.data.webservice.customer.CustomerService
import com.drugstore.data.webservice.customer.CustomerServiceProxy
import com.drugstore.data.webservice.front.FrontService
import com.drugstore.data.webservice.front.FrontServiceProxy
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.Call
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object WebserviceModule {

    @Provides
    @Singleton
    fun authService(
        errorHandler: ErrorHandler,
        authHandler: AuthHandler,
        retrofit: Retrofit
    ): AuthService =
        AuthServiceProxy(
            errorHandler,
            authHandler,
            retrofit.newBuilder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .build()
                .create()
        )

    @Provides
    @Singleton
    fun customerService(
        errorHandler: ErrorHandler,
        authHandler: AuthHandler,
        retrofit: Retrofit
    ): CustomerService =
        CustomerServiceProxy(
            errorHandler,
            authHandler,
            retrofit.newBuilder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .build()
                .create()
        )

    @Provides
    @Singleton
    fun frontService(
        errorHandler: ErrorHandler,
        retrofit: Retrofit
    ): FrontService =
        FrontServiceProxy(
            errorHandler,
            retrofit.newBuilder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .build()
                .create()
        )

    @Provides
    @Singleton
    fun putLoginService(retrofit: Retrofit): PutLoginService = retrofit.newBuilder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .build()
        .create()

    @Provides
    @Singleton
    fun retrofit(
        callFactory: Call.Factory,
        converterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://override.com")
        .callFactory(callFactory)
        .addConverterFactory(converterFactory)
        .build()

    @Provides
    @Singleton
    fun callFactory(
        headerInterceptor: HeaderInterceptor,
        chuckInterceptor: ChuckInterceptor
    ): Call.Factory = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(chuckInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun chuckInterceptor(application: Application) = ChuckInterceptor(application)

    @Provides
    @Singleton
    fun converterFactory(moshi: Moshi): Converter.Factory = MoshiConverterFactory.create(moshi)

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder().build()
}