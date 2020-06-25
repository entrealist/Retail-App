package com.kasprov.android.core.config.activity

import androidx.annotation.IdRes
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class DrawerLayout(@IdRes val value: Int)