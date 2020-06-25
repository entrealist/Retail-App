package com.drugstore.data.webservice.front.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetStaticInfoCommonResponse(
    @Json(name = "add_to_card") val addToCard: String,
    @Json(name = "start_reorder") val startReorder: String,
    @Json(name = "prescription_defaults") val prescriptionDefaults: String
)