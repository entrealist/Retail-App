package com.drugstore.data.webservice

import com.drugstore.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeaderInterceptor @Inject constructor(
    private val sessionHolder: SessionHolder
) : Interceptor {
    private val headers = BuildConfig.API_HEADERS

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBuilder = request.newBuilder()

        newBuilder.apply {
            headers.forEach { header(it.key, it.value) }
            if (request.needsAuthentication()) sessionHolder.token?.let { header(AUTH_HEADER_NAME, it) }
            if (request.needsLocale()) sessionHolder.localeId?.let { header(LOCALE_HEADER_NAME, it) }
            removeHeader(NO_AUTH_HEADER_NAME)
            removeHeader(NO_LOCALE_HEADER_NAME)
        }

        return chain.proceed(newBuilder.build())
    }

    private fun Request.needsAuthentication() = header(NO_AUTH_HEADER_NAME) == null

    private fun Request.needsLocale() = header(NO_LOCALE_HEADER_NAME) == null
}

const val AUTH_HEADER_NAME =
    BuildConfig.AUTH_HEADER_NAME
private const val LOCALE_HEADER_NAME =
    BuildConfig.LOCALE_HEADER_NAME

private const val NO_AUTH_HEADER_NAME = "No-Authentication"
const val NO_AUTH_HEADER = "$NO_AUTH_HEADER_NAME: true"

private const val NO_LOCALE_HEADER_NAME = "No-Locale"
const val NO_LOCALE_HEADER = "$NO_LOCALE_HEADER_NAME: true"