package com.kasprov.android.core.recyclerview.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class ViewBinder<T : Any, VH : RecyclerView.ViewHolder> : DiffUtil.ItemCallback<T>() {
    private val viewType by lazy { View.generateViewId() }
    open fun getItemViewType(item: T) = viewType
    abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    abstract fun onBindViewHolder(holder: VH, item: T)
}