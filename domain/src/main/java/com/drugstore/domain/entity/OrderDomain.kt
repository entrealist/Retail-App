package com.drugstore.domain.entity

data class OrderDomain(
    val order: Order,
    val products: List<OrderProduct>,
    val isReorderPossible: Boolean
)