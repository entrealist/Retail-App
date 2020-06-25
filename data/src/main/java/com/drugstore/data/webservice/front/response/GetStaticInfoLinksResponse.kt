package com.drugstore.data.webservice.front.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetStaticInfoLinksResponse(
    @Json(name = "terms_and_conditions") val termsAndConditions: String,
    @Json(name = "privacy") val privacy: String
)