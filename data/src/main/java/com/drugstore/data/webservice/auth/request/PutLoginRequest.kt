package com.drugstore.data.webservice.auth.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PutLoginRequest(
    @Json(name = "token") val token: String,
    @Json(name = "refresh_token") val refreshToken: String
)