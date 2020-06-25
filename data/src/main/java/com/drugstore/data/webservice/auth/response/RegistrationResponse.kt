package com.drugstore.data.webservice.auth.response

import com.drugstore.data.webservice.model.CustomerModel
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RegistrationResponse(
    @Json(name = "login") val login: Login,
    @Json(name = "customer") val customer: CustomerModel
) {

    class Login(
        @Json(name = "token") val token: String,
        @Json(name = "refresh_token") val refreshToken: String,
        @Json(name = "expire_at") val expireAt: Long
    )
}