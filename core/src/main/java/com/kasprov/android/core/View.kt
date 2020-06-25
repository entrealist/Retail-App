package com.kasprov.android.core

import android.view.KeyEvent
import android.view.View
import androidx.core.view.ViewCompat

inline fun View.setOnEnterKeyDownListener(crossinline listener: () -> Unit) =
    setOnKeyListener(KeyEvent.KEYCODE_ENTER, KeyEvent.ACTION_DOWN, listener)

inline fun View.setOnKeyListener(triggerKeyCode: Int, triggerEventAction: Int, crossinline listener: () -> Unit) {
    setOnKeyListener { _, keyCode, event ->
        if (keyCode == triggerKeyCode && event.action == triggerEventAction) {
            listener.invoke()
            true
        } else {
            false
        }
    }
}

fun View.applySystemWindowInsets(flags: Int) {
    val flagLeft = resources.getInteger(R.integer.flag_left)
    val flagTop = resources.getInteger(R.integer.flag_top)
    val flagRight = resources.getInteger(R.integer.flag_right)
    val flagBottom = resources.getInteger(R.integer.flag_bottom)

    val initialPaddingLeft = paddingLeft
    val initialPaddingTop = paddingTop
    val initialPaddingRight = paddingRight
    val initialPaddingBottom = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
        val paddingLeft = if ((flags and flagLeft) == flagLeft) {
            initialPaddingLeft + insets.systemWindowInsetLeft
        } else {
            v.paddingLeft
        }
        val paddingTop = if ((flags and flagTop) == flagTop) {
            initialPaddingTop + insets.systemWindowInsetTop
        } else {
            v.paddingTop
        }
        val paddingRight = if ((flags and flagRight) == flagRight) {
            initialPaddingRight + insets.systemWindowInsetRight
        } else {
            v.paddingRight
        }
        val paddingBottom = if ((flags and flagBottom) == flagBottom) {
            initialPaddingBottom + insets.systemWindowInsetBottom
        } else {
            v.paddingBottom
        }

        if (v.paddingLeft != paddingLeft || v.paddingTop != paddingTop ||
            v.paddingRight != paddingRight || v.paddingBottom != paddingBottom
        ) v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)

        return@setOnApplyWindowInsetsListener insets
    }
}