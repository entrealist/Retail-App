package com.drugstore.app.core.viewbinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.drugstore.app.core.item.LoadingItem
import com.drugstore.databinding.TemplateLoadingIndicatorBinding
import com.kasprov.android.core.recyclerview.adapter.ViewDataBindingViewBinder

class LoadingItemViewBinder(
    private val itemHeight: Int? = null
) : ViewDataBindingViewBinder<LoadingItem, TemplateLoadingIndicatorBinding>() {
    override fun areItemsTheSame(oldItem: LoadingItem, newItem: LoadingItem) = true
    override fun areContentsTheSame(oldItem: LoadingItem, newItem: LoadingItem) = true

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): TemplateLoadingIndicatorBinding {
        val binding = TemplateLoadingIndicatorBinding.inflate(inflater, parent, false)
        if (itemHeight != null) binding.root.updateLayoutParams {
            height = itemHeight
        }
        return binding
    }

    override fun bind(binding: TemplateLoadingIndicatorBinding, item: LoadingItem) {
        binding.isVisible = true
    }
}