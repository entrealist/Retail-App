package com.kasprov.android.core.recyclerview.itemdecoration

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class SwipeItemTouchCallback<T>(
    private val adapter: ListAdapter<T, *>,
    private val background: Drawable,
    private val icon: Drawable,
    private val onSwipe: (T) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (position >= 0 && position < adapter.currentList.size) {
            onSwipe(adapter.currentList[position])
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView

        val iconMargin = icon.intrinsicWidth
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        if (dX < 0) { // Swiping to the left
            val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0)
            icon.setBounds(0, 0, 0, 0)
        }

        background.draw(c)
        icon.draw(c)
    }
}