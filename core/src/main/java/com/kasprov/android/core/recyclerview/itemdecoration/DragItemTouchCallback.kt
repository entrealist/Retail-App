package com.kasprov.android.core.recyclerview.itemdecoration

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DragItemTouchCallback<T>(
    private val adapter: ListAdapter<T, *>
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val newList: List<T> = ArrayList(adapter.currentList)
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) Collections.swap(newList, i, i + 1)
        } else {
            for (i in fromPosition downTo toPosition + 1) Collections.swap(newList, i, i - 1)
        }
        adapter.submitList(newList)
        return true
    }

    override fun isItemViewSwipeEnabled() = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
}