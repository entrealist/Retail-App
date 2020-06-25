package com.kasprov.android.core.recyclerview.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executor

open class SimplePagedAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    private val viewBinder: ViewBinder<T, VH>,
    backgroundThreadExecutor: Executor? = null
) : PagedListAdapter<T, VH>(
    AsyncDifferConfig.Builder(viewBinder)
        .setBackgroundThreadExecutor(backgroundThreadExecutor)
        .build()
) {

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position) ?: throw IllegalArgumentException("$this doesn't support placeholders")
        return viewBinder.getItemViewType(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        viewBinder.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position) ?: throw IllegalArgumentException("$this doesn't support placeholders")
        viewBinder.onBindViewHolder(holder, item)
    }
}