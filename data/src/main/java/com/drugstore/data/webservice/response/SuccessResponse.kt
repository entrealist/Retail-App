package com.drugstore.data.webservice.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class SuccessResponse(
    @Json(name = "message") val message: String
)