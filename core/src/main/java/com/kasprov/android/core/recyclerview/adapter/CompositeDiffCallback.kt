package com.kasprov.android.core.recyclerview.adapter

import androidx.recyclerview.widget.DiffUtil

internal class CompositeDiffCallback<T : Any>(
    private val diffCallbacks: Map<Class<out T>, DiffUtil.ItemCallback<out T>>
) : DiffUtil.ItemCallback<T>() {

    @Suppress("UNCHECKED_CAST")
    private fun getDiffCallback(item: T) = diffCallbacks.getValue(item.javaClass) as DiffUtil.ItemCallback<T>

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        if (oldItem::class != newItem::class) return false
        return getDiffCallback(oldItem).areItemsTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        // We know the items are the same class because [areItemsTheSame] returned true
        return getDiffCallback(oldItem).areContentsTheSame(oldItem, newItem)
    }
}