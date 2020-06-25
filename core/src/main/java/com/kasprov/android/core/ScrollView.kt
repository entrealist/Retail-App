package com.kasprov.android.core

import android.view.View
import android.widget.ScrollView

fun ScrollView.scrollToTopOf(view: View) {
    smoothScrollTo(0, view.top)
}

fun ScrollView.scrollToBottom() {
    if (childCount == 0) {
        return
    }
    val lastChild = getChildAt(childCount - 1)
    val delta = lastChild.bottom - (scrollY + height)
    smoothScrollBy(0, delta)
}