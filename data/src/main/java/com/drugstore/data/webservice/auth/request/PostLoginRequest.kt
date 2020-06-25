package com.drugstore.data.webservice.auth.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PostLoginRequest(
    @Json(name = "email") val email: String?,
    @Json(name = "password") val password: String?,
    @Json(name = "guard") val guard: String = "customer"
)