package com.drugstore.app.main.orders

import android.content.Context
import androidx.core.content.ContextCompat
import com.drugstore.R

private val SUCCESS_STATUSES = arrayOf(
    "completed", "delivered"
)
private val FAILURE_STATUSES = arrayOf(
    "black_list", "cancelled", "prescription_rejected", "rejected_by_pharmacy", "fraud"
)

private fun String.toColorResId() = when (this) {
    in SUCCESS_STATUSES -> R.color.status_success
    in FAILURE_STATUSES -> R.color.status_danger
    else -> R.color.status_warning
}

fun Context.statusColor(statusId: String?) =
    statusId?.toColorResId()?.let { ContextCompat.getColor(this, it) }