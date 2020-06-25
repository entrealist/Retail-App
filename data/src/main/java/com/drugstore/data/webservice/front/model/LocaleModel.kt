package com.drugstore.data.webservice.front.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class LocaleModel(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "country_id") val countryId: String
)