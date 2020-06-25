package com.drugstore.data.webservice.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CustomerModel(
    @Json(name = "id") val id: Int,
    @Json(name = "uuid") val uuid: String,
    @Json(name = "email") val email: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "last_name") val lastName: String,
    @Json(name = "gender") val gender: Int,
    @Json(name = "birthday_date") val birthdayDate: String,
    @Json(name = "locale_id") val localeId: String
)