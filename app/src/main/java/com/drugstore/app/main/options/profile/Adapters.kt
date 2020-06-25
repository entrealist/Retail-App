package com.drugstore.app.main.options.profile

import com.drugstore.domain.entity.User

fun User?.name() = this?.run { "$firstName $lastName" }