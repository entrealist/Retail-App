package com.drugstore.data.webservice.auth.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PostFrontResponse (
    @Json(name = "token") val token: String
)