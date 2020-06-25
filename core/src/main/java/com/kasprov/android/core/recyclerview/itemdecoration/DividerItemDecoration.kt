package com.kasprov.android.core.recyclerview.itemdecoration

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class DividerItemDecoration(
    private val divider: Drawable,
    private val orientation: Int = LinearLayout.HORIZONTAL,
    @param:Px private val paddingEnd: Int = 0,
    @param:Px private val paddingStart: Int = 0,
    private val isLastDrawn: Boolean = false,
    private val isCentered: Boolean = false
) : RecyclerView.ItemDecoration() {

    private val bounds = Rect()
    private val horizontalDividerOffset: Int get() = if (isCentered) divider.intrinsicHeight / 2 else 0
    private val verticalDividerOffset: Int get() = if (isCentered) divider.intrinsicWidth / 2 else 0

    constructor(
        divider: Drawable,
        orientation: Int = LinearLayout.HORIZONTAL,
        @Px padding: Int = 0,
        isLastDrawn: Boolean = false,
        isCentered: Boolean = false
    ) : this(divider, orientation, padding, padding, isLastDrawn, isCentered)

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null) return
        if (orientation == LinearLayout.HORIZONTAL) {
            drawHorizontal(canvas, parent)
        } else {
            drawVertical(canvas, parent)
        }
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()

        var left: Int
        var right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }
        right -= paddingEnd
        left += paddingStart

        val childCount = parent.childCount
        val spanCount = getSpanCount(parent)

        for (i in 0 until childCount) {
            if (isHorizontalDividerDrawnFor(i, spanCount, childCount)) {
                val child = parent.getChildAt(i)
                parent.getDecoratedBoundsWithMargins(child, bounds)
                val bottom = bounds.bottom + child.translationY.roundToInt() + horizontalDividerOffset
                val top = bottom - divider.intrinsicHeight
                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            }
        }

        canvas.restore()
    }

    private fun isHorizontalDividerDrawnFor(position: Int, spanCount: Int, totalCount: Int) =
        isLastDrawn || !isInTheLastRow(position, spanCount, totalCount)

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()

        val childCount = parent.childCount
        val spanCount = getSpanCount(parent)

        for (i in 0 until childCount) {
            if (isVerticalDividerDrawnFor(i, spanCount)) {
                val child = parent.getChildAt(i)
                parent.layoutManager?.getDecoratedBoundsWithMargins(child, bounds)
                val bottom = bounds.bottom - paddingEnd
                val right = bounds.right + child.translationX.roundToInt() + verticalDividerOffset
                val left = right - divider.intrinsicWidth
                val top = bounds.top + paddingStart
                divider.setBounds(left, top, right, bottom)
                divider.draw(canvas)
            }
        }

        canvas.restore()
    }

    private fun isVerticalDividerDrawnFor(position: Int, spanCount: Int) =
        isLastDrawn || !isLastInRow(position, spanCount)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (orientation == LinearLayout.HORIZONTAL) {
            outRect.set(0, 0, 0, divider.intrinsicHeight)
        } else {
            outRect.set(0, 0, divider.intrinsicWidth, 0)
        }
    }

    companion object {

        private fun isLastInRow(position: Int, spanCount: Int) =
            (position + 1) % spanCount == 0

        private fun isInTheLastRow(position: Int, spanCount: Int, totalCount: Int) =
            position >= totalCount - spanCount

        private fun getSpanCount(parent: RecyclerView) =
            (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1
    }
}