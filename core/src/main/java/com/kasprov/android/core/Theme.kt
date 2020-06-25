package com.kasprov.android.core

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes

internal fun Resources.Theme.resolveAttribute(@AttrRes resId: Int, resolveRefs: Boolean = true) =
    TypedValue().run {
        resolveAttribute(resId, this, resolveRefs)
        data
    }

internal fun Resources.Theme.resolveBooleanAttribute(@AttrRes resId: Int) = TypedValue().run {
    resolveAttribute(resId, this, true)
    if (type == TypedValue.TYPE_INT_BOOLEAN) {
        data != 0
    } else {
        null
    }
}