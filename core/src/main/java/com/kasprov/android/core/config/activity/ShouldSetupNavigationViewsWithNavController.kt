package com.kasprov.android.core.config.activity

import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class ShouldSetupNavigationViewsWithNavController(val value: Boolean)