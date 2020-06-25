package com.drugstore.domain

import com.drugstore.domain.entity.OrderDomain
import com.drugstore.domain.entity.OrderWithProducts

fun OrderWithProducts.map() =
    OrderDomain(
        order,
        products,
        order.statusId == "delivered"
    )