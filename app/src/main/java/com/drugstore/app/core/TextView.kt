package com.drugstore.app.core

import android.text.method.LinkMovementMethod
import android.widget.TextView

fun TextView.setLinkMovementMethod() {
    movementMethod = LinkMovementMethod.getInstance()
}