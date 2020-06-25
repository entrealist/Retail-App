package com.drugstore.app.main.options.settings.region

import android.content.Context
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.style.TextAppearanceSpan
import androidx.annotation.StyleRes
import com.drugstore.R

fun Context.regionName(text: CharSequence, isSelected: Boolean): CharSequence? {
    return SpannableString(text).apply {
        @StyleRes val textAppearanceResId = if (isSelected) {
            R.style.regular_text_medium15px_left
        } else {
            R.style.regular_text_regular15px_left
        }
        setSpan(TextAppearanceSpan(this@regionName, textAppearanceResId), 0, length, SPAN_INCLUSIVE_INCLUSIVE)
    }
}