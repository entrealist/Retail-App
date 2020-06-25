@file:JvmName("AppAdapters")

package com.drugstore.app.core

import android.content.Context
import com.drugstore.R
import com.drugstore.domain.entity.Address
import com.drugstore.domain.entity.Address.Type.DHL_PACKSTATION
import com.drugstore.domain.entity.Address.Type.REGULAR
import com.drugstore.domain.entity.Gender
import com.drugstore.domain.entity.Gender.FEMALE
import com.drugstore.domain.entity.Gender.MALE
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

private val DATE_1_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
fun LocalDate?.date1() = this?.format(DATE_1_FORMATTER)

private val DATE_TIME_1_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyy 'at' HH:mm")
fun LocalDateTime?.dateTime1() = this?.format(DATE_TIME_1_FORMATTER)

private fun Gender.toNameResId() = when (this) {
    MALE -> R.string.gender_male
    FEMALE -> R.string.gender_female
}

fun Context.genderName(gender: Gender?) = gender?.toNameResId()?.let { getString(it) }

private fun Address.Type.toNameResId() = when (this) {
    REGULAR -> R.string.address_type_regular
    DHL_PACKSTATION -> R.string.address_type_dhl_packstation
}

fun Context.addressTypeName(addressType: Address.Type?) = addressType?.toNameResId()?.let { getString(it) }
