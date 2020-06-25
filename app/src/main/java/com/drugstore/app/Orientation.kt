package com.drugstore.app

import android.app.Activity
import android.content.pm.ActivityInfo
import com.drugstore.R

fun Activity.validateOrientation() {
    val isTablet = resources.getBoolean(R.bool.is_tablet)
    if (!isTablet) requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
}