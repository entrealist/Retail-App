package com.drugstore.data.webservice.auth.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PutLoginResponse (
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "expire_at") val expireAt: Long
)