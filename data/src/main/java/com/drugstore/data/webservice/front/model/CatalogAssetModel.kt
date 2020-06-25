package com.drugstore.data.webservice.front.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CatalogAssetModel(
    @Json(name = "type") val type: String,
    @Json(name = "src") val src: String
)