package com.drugstore.app.core

import android.view.View
import android.widget.ImageView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.kasprov.android.core.applySystemWindowInsets

@BindingAdapter("android:bind_imageUri")
fun ImageView.loadImageUri(imageUri: String?) {
    Glide.with(context)
        .load(imageUri)
        .into(this)
}

@BindingAdapter("android:bind_visible")
fun ContentLoadingProgressBar.setVisible(isVisible: Boolean?) =
    if (isVisible == true) show() else hide()

@BindingAdapter("android:bind_applySystemWindowInsets")
fun View.applySystemWindowInsets(flags: Int) = applySystemWindowInsets(flags)