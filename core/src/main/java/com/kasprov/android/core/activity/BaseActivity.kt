package com.kasprov.android.core.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.*
import com.google.android.material.navigation.NavigationView
import com.kasprov.android.core.config.activity.ShouldSetupNavigationViewsWithNavController

@ShouldSetupNavigationViewsWithNavController(true)
abstract class BaseActivity : AppCompatActivity() {

    private val config by lazy { collectConfig() }

    protected open val navController get() = config.navHostFragmentResId?.let { safeFindNavController(it) }

    private val drawerLayout get() = config.drawerLayoutResId?.let { findViewById<DrawerLayout>(it) }
    private val navigationView get() = config.navigationViewResId?.let { findViewById<NavigationView>(it) }

    private var appBarConfiguration: AppBarConfiguration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onSetContentView()

        setSupportActionBar(toolbar)

        if (savedInstanceState == null && config.shouldSetupNavigationViewsWithNavController == true)
            navController?.setupWithNavigationViews()
    }

    protected abstract fun onSetContentView()

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (config.shouldSetupNavigationViewsWithNavController == true)
            navController?.setupWithNavigationViews()
    }

    protected fun NavController.setupWithNavigationViews() {
        appBarConfiguration = supportActionBar?.let {
            config.topLevelDestinationsResIds?.run {
                AppBarConfiguration.Builder(toSet())
            } ?: AppBarConfiguration.Builder(graph)
        }?.run {
            setDrawerLayout(drawerLayout).build()
        }?.also {
            setupActionBarWithNavController(this@BaseActivity, this, it)
        }

        navigationView?.let { setupWithNavController(it, this) }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navigateUp = navController?.let {
            appBarConfiguration?.run { navigateUp(it, this) }
        } ?: false
        return navigateUp || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val onNavDestinationSelected = navController?.let { onNavDestinationSelected(item, it) } ?: false
        return onNavDestinationSelected || super.onOptionsItemSelected(item)
    }

    protected fun navigateTo(directions: NavDirections) = navController?.navigate(directions)
}