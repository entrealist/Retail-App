package com.drugstore.app.main.categories.products.product

import com.drugstore.domain.entity.ProductTwo

fun ProductTwo?.label() = this?.let { "$quantityString - $priceString" }