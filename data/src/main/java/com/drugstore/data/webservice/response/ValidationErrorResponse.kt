package com.drugstore.data.webservice.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ValidationErrorResponse(
    @Json(name = "message") val message: String,
    @Json(name = "errors") val errors: Map<String, Array<String>>?
)