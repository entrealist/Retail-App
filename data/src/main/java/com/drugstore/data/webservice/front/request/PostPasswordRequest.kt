package com.drugstore.data.webservice.front.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PostPasswordRequest(
    @Json(name = "email") val email: String?
)