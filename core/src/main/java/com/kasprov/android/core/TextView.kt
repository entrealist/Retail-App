package com.kasprov.android.core

import android.view.inputmethod.EditorInfo
import android.widget.TextView

inline fun TextView.setOnEditorActionDoneListener(crossinline listener: () -> Unit) =
    setOnEditorActionListener(EditorInfo.IME_ACTION_DONE, listener)

inline fun TextView.setOnEditorActionSearchListener(crossinline listener: () -> Unit) =
    setOnEditorActionListener(EditorInfo.IME_ACTION_SEARCH, listener)

inline fun TextView.setOnEditorActionListener(triggerActionId: Int, crossinline listener: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == triggerActionId) {
            listener.invoke()
            true
        } else {
            false
        }
    }
}