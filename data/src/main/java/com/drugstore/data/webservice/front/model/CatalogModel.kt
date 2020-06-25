package com.drugstore.data.webservice.front.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class CatalogModel(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "short_description") val shortDescription: String,
    @Json(name = "children") val children: Array<Int>,
    @Json(name = "root_id") val rootId: Int?,
    @Json(name = "assets") val assets: Array<CatalogAssetModel>,
    @Json(name = "items") val items: Array<CatalogItemModel>
)