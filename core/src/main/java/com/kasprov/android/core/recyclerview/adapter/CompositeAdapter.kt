package com.kasprov.android.core.recyclerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executor

open class CompositeAdapter<T : Any, VH : RecyclerView.ViewHolder>(
    private val viewBinders: Map<Class<out T>, ViewBinder<out T, out VH>>,
    backgroundThreadExecutor: Executor? = null
) : ListAdapter<T, VH>(
    AsyncDifferConfig.Builder(CompositeDiffCallback(viewBinders))
        .setBackgroundThreadExecutor(backgroundThreadExecutor)
        .build()
) {
    constructor(
        vararg viewBinders: Pair<Class<out T>, ViewBinder<out T, out VH>>,
        backgroundThreadExecutor: Executor? = null
    ) : this(mapOf(*viewBinders), backgroundThreadExecutor)

    private val viewTypeToViewBinderMap = mutableMapOf<Int, ViewBinder<out T, out VH>>()

    @Suppress("UNCHECKED_CAST")
    private fun getViewBinder(item: T) = viewBinders.getValue(item.javaClass) as ViewBinder<T, VH>

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        val viewBinder = getViewBinder(item)
        val viewType = viewBinder.getItemViewType(item)
        viewTypeToViewBinderMap[viewType] = viewBinder
        return viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        viewTypeToViewBinderMap.getValue(viewType).onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        getViewBinder(item).onBindViewHolder(holder, item)
    }
}

inline fun <reified T : Any, VH : RecyclerView.ViewHolder> ViewBinder<T, VH>.toCompositeAdapter(
    backgroundThreadExecutor: Executor? = null
) = CompositeAdapter(T::class.java to this, backgroundThreadExecutor = backgroundThreadExecutor)