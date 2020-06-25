package com.drugstore.app.main.options.profile.addresses

import android.content.Context
import com.drugstore.R
import com.drugstore.domain.entity.Address
import com.drugstore.domain.entity.Address.Type.DHL_PACKSTATION
import com.drugstore.domain.entity.Address.Type.REGULAR

fun Address?.addressLine1() = this?.run {
    "$firstName $lastName"
}

fun Context.addressLine2(address: Address?) = address?.run {
    when(type) {
        REGULAR -> "$street $houseNumber, $postalCode"
        DHL_PACKSTATION -> "${getString(R.string.address_street_dhl_packstation)} $packstationNumber, $packstationAddress, $postalCode"
    }
}

fun Address?.addressLine3() = this?.run {
    "$city, ${country.name}"
}