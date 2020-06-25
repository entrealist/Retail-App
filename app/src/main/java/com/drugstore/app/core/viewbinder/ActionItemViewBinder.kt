package com.drugstore.app.core.viewbinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.drugstore.app.core.item.ActionItem
import com.drugstore.databinding.TemplateActionBinding
import com.kasprov.android.core.recyclerview.adapter.ViewDataBindingViewBinder

object ActionItemViewBinder : ViewDataBindingViewBinder<ActionItem, TemplateActionBinding>() {
    override fun areItemsTheSame(oldItem: ActionItem, newItem: ActionItem) = oldItem == newItem
    override fun areContentsTheSame(oldItem: ActionItem, newItem: ActionItem) = true

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) =
        TemplateActionBinding.inflate(inflater, parent, false)

    override fun bind(binding: TemplateActionBinding, item: ActionItem) {
        binding.title = item.title
        binding.action = item.action
        binding.onClick = item.onClick
    }
}