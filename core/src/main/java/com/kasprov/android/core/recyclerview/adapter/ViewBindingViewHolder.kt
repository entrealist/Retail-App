package com.kasprov.android.core.recyclerview.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class ViewBindingViewHolder<out B : ViewBinding>(val binding: B) :
    RecyclerView.ViewHolder(binding.root)