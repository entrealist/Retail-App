package com.drugstore.data.webservice.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ErrorResponse(
    @Json(name = "code") val code: Int?,
    @Json(name = "message") val message: String
)