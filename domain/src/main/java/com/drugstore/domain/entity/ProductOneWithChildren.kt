package com.drugstore.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Relation

data class ProductOneWithChildren(
    val id: Int,
    @ColumnInfo(name = "product_id", index = true) val productId: Int,
    @ColumnInfo(name = "title") val title: String,
    @Relation(parentColumn = "id", entityColumn = "product_one_id", entity = ProductTwo::class)
    var children: List<ProductTwo>
)