package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Relation

data class ProductWithChildren(
    var id: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "children_image_uris") var childrenImageUris: List<String>,
    @Relation(parentColumn = "id", entityColumn = "product_id", entity = ProductOne::class)
    var children: List<ProductOneWithChildren>
)