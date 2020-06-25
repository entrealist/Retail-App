package com.kasprov.android.core.lifecycle

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
class Event<T> internal constructor(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    val contentIfNotHandled: T?
        get() = if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }

    init {
        if (content == null) {
            throw IllegalArgumentException("null values in Event are not allowed.")
        }
    }
}