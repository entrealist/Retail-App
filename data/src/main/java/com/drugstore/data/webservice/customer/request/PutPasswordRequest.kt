package com.drugstore.data.webservice.customer.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PutPasswordRequest(
    @Json(name = "old_password") val oldPassword: String?,
    @Json(name = "password") val password: String?,
    @Json(name = "password_confirmation") val passwordConfirmation: String?
)