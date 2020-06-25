package com.drugstore.data.webservice.front.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetStaticInfoLegalResponse(
    @Json(name = "emails") val emails: Array<String>?,
    @Json(name = "phones") val phones: Array<String>?,
    @Json(name = "working_hours") val workingHours: String?,
    @Json(name = "addresses") val addresses: Array<String>?,
    @Json(name = "agree_description") val agreeDescription: String?
)