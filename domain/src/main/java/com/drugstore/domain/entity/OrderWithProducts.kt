package com.drugstore.domain.entity

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithProducts(
    @Embedded val order: Order,
    @Relation(parentColumn = "id", entityColumn = "order_id", entity = OrderProduct::class)
    var products: List<OrderProduct>
)