package com.drugstore.data.webservice.auth.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RegistrationRequest(
    @Json(name = "email") val email: String?,
    @Json(name = "first_name") val firstName: String?,
    @Json(name = "last_name") val lastName: String?,
    @Json(name = "password") val password: String?,
    @Json(name = "password_confirmation") val passwordConfirmation: String?,
    @Json(name = "gender") val gender: Int?,
    @Json(name = "birthday") val birthday: String?
)