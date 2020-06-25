package com.kasprov.android.core.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executor

open class SimpleAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    private val viewBinder: ViewBinder<T, VH>,
    backgroundThreadExecutor: Executor? = null
) : ListAdapter<T, VH>(
    AsyncDifferConfig.Builder(viewBinder)
        .setBackgroundThreadExecutor(backgroundThreadExecutor)
        .build()
) {

    override fun getItemViewType(position: Int) =
        viewBinder.getItemViewType(getItem(position))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        viewBinder.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: VH, position: Int) =
        viewBinder.onBindViewHolder(holder, getItem(position))
}