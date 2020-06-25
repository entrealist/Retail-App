package com.kasprov.android.core

import android.view.View

fun Boolean?.visibleElseGone() = if (this == true) View.VISIBLE else View.GONE

fun Boolean?.visibleElseInvisible() = if (this == true) View.VISIBLE else View.INVISIBLE

fun Boolean?.goneElseVisible() = if (this == true) View.GONE else View.VISIBLE