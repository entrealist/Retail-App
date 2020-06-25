package com.kasprov.android.core.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

abstract class ViewBindingViewBinder<T : Any, B : ViewBinding> :
    ViewBinder<T, ViewBindingViewHolder<B>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingViewHolder<B> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = createBinding(inflater, parent, viewType)
        return ViewBindingViewHolder(binding)
    }

    abstract fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): B

    override fun onBindViewHolder(holder: ViewBindingViewHolder<B>, item: T) {
        bind(holder.binding, item)
    }

    protected open fun bind(binding: B, item: T) {}
}