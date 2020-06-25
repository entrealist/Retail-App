package com.kasprov.android.core.recyclerview.adapter

import androidx.databinding.ViewDataBinding

abstract class ViewDataBindingViewBinder<T : Any, B : ViewDataBinding> :
    ViewBindingViewBinder<T, B>() {

    override fun onBindViewHolder(holder: ViewBindingViewHolder<B>, item: T) {
        super.onBindViewHolder(holder, item)
        holder.binding.executePendingBindings()
    }
}