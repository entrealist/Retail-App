package com.kasprov.android.core.recyclerview

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.calculateLayoutParamsHeightForSpanCount(
    layoutParams: RecyclerView.LayoutParams,
    spanCount: Int
): Int {
    val availableRecyclerViewHeight = height - paddingBottom - paddingTop
    val layoutParamsVerticalMargin = layoutParams.bottomMargin + layoutParams.topMargin
    return availableRecyclerViewHeight / spanCount - layoutParamsVerticalMargin
}

fun RecyclerView.calculateLayoutParamsWidthForSpanCount(
    layoutParams: RecyclerView.LayoutParams,
    spanCount: Int
): Int {
    val availableRecyclerViewWidth = width - paddingEnd - paddingStart
    val layoutParamsHorizontalMargin = layoutParams.marginEnd + layoutParams.marginStart
    return availableRecyclerViewWidth / spanCount - layoutParamsHorizontalMargin
}