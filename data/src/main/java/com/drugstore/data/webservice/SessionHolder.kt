package com.drugstore.data.webservice

interface SessionHolder {
    val localeId: String?
    val token: String?
    val refreshToken: String?
    val tokenExpirationTimestamp: Long
    fun sessionUpdated(refreshToken: String, expireAt: Long)
    fun sessionInvalidated()
}