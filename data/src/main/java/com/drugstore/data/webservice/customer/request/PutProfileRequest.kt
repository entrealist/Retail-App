package com.drugstore.data.webservice.customer.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PutProfileRequest(
    @Json(name = "email") val email: String?
)