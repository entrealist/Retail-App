package com.drugstore.data.webservice.front.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PostContactUsRequest(
    @Json(name = "content") val content: String?,
    @Json(name = "email") val email: String?,
    @Json(name = "name") val name: String?
)