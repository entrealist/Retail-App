package com.kasprov.android.core.activity

import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import com.kasprov.android.core.config.activity.*

internal class ActivityConfig internal constructor(
    @param:IdRes @field:IdRes val navHostFragmentResId: Int?,
    val shouldSetupNavigationViewsWithNavController: Boolean?,
    val topLevelDestinationsResIds: IntArray?,
    @param:IdRes @field:IdRes val drawerLayoutResId: Int?,
    @param:IdRes @field:IdRes val navigationViewResId: Int?
)

internal fun ComponentActivity.collectConfig() = ActivityConfig(
    javaClass.getAnnotation(NavHostFragment::class.java)?.value,
    javaClass.getAnnotation(ShouldSetupNavigationViewsWithNavController::class.java)?.value,
    javaClass.getAnnotation(TopLevelDestinations::class.java)?.value,
    javaClass.getAnnotation(DrawerLayout::class.java)?.value,
    javaClass.getAnnotation(NavigationView::class.java)?.value
)