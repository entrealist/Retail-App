package com.drugstore.app.main.options.contactus

import android.content.Context
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.set
import com.drugstore.R
import com.drugstore.domain.entity.ContactDetail
import com.drugstore.domain.entity.ContactDetail.Type.*

private fun ContactDetail.Type.toIconResId() = when (this) {
    ADDRESS -> R.drawable.ic_location_on
    EMAIL -> R.drawable.ic_email
    PHONE -> R.drawable.ic_phone
    SCHEDULE -> R.drawable.ic_access_time
}

fun Context.contactDetailTypeIcon(type: ContactDetail.Type?) =
    type?.toIconResId()?.let { ContextCompat.getDrawable(this, it) }

fun String?.data(type: ContactDetail.Type?): CharSequence? = this?.run {
    type?.let { type ->
        when (type) {
            ADDRESS -> SpannableString(this).also {
                it[0..length] = URLSpan("geo://?q=${Uri.encode(this)}")
            }
            EMAIL -> SpannableString(this).also {
                it[0..length] = URLSpan("mailto:$this")
            }
            PHONE -> SpannableString(this).also {
                it[0..length] = URLSpan("tel:$this")
            }
            SCHEDULE -> this
        }
    }
}

fun TextView?.triggerClickableSpan() = this?.apply {
    val spanned = text as? Spanned
    val urlSpans = spanned?.getSpans(0, text.length, ClickableSpan::class.java)
    urlSpans?.firstOrNull()?.onClick(this)
}