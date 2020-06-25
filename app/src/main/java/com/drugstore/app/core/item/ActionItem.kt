package com.drugstore.app.core.item

import android.view.View

data class ActionItem(
    val title: String,
    val action: String,
    val onClick: View.OnClickListener
)