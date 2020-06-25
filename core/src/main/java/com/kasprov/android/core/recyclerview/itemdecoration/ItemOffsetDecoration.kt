package com.kasprov.android.core.recyclerview.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

class ItemOffsetDecoration(
    @Px private val left: Int = 0,
    @Px private val top: Int = 0,
    @Px private val right: Int = 0,
    @Px private val bottom: Int = 0
) : RecyclerView.ItemDecoration() {

    constructor(@Px size: Int) : this(size, size, size, size)

    constructor(@Px horizontal: Int = 0, @Px vertical: Int = 0) : this(horizontal, vertical, horizontal, vertical)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = left
        outRect.top = top
        outRect.right = right
        outRect.bottom = bottom
    }
}