package com.kasprov.android.core.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.kasprov.android.core.config.activity.Toolbar
import com.kasprov.android.core.lifecycle.Event
import com.kasprov.android.core.lifecycle.observeEvent
import com.kasprov.android.core.lifecycle.observeNonNull
import com.kasprov.android.core.resolveAttribute
import com.kasprov.android.core.resolveBooleanAttribute

fun <B : ViewDataBinding> ComponentActivity.viewDataBindings(bindingProducer: () -> B): Lazy<B> =
    ComponentActivityViewDataBindingLazy(this, bindingProducer)

inline fun <T> ComponentActivity.observe(
    liveData: LiveData<out T?>,
    crossinline onChanged: (T?) -> Unit
) = liveData.observe(this, Observer { onChanged(it) })

inline fun <T> ComponentActivity.observeNonNull(
    liveData: LiveData<out T?>,
    crossinline onChanged: (T) -> Unit
) = liveData.observeNonNull(this, onChanged)

inline fun <T> ComponentActivity.observeEvent(
    liveData: LiveData<out Event<T>?>,
    crossinline onChanged: (T) -> Unit
) = liveData.observeEvent(this, onChanged)

/**
 * If NavHostFragment was inflated with FragmentContainerView then NavController is unavailable
 * during Activity.onCreate. This method allows to find NavController even at that point.
 * https://issuetracker.google.com/issues/142847973
 */
fun FragmentActivity.safeFindNavController(navHostFragmentResId: Int): NavController? {
    val navHostFragment = supportFragmentManager.findFragmentById(navHostFragmentResId) as? NavHostFragment
    return navHostFragment?.navController
}

fun Activity.setFullscreenSystemUiVisibility() {
    window.decorView.systemUiVisibility =
        window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
}

val Activity.themeStatusBarColor
    get() = theme.resolveAttribute(android.R.attr.statusBarColor, false)

val Activity.themeNavigationBarColor
    get() = theme.resolveAttribute(android.R.attr.navigationBarColor, false)

val Activity.themeWindowLightStatusBar
    @SuppressLint("InlinedApi")
    get() = theme.resolveBooleanAttribute(android.R.attr.windowLightStatusBar) ?: false

val Activity.themeWindowLightNavigationBar
    @SuppressLint("InlinedApi")
    get() = theme.resolveBooleanAttribute(android.R.attr.windowLightNavigationBar) ?: false

fun Activity.setStatusBarColorResource(@ColorRes colorResId: Int) {
    window.statusBarColor = ContextCompat.getColor(this, colorResId)
}

fun Activity.setNavigationBarColorResource(@ColorRes colorResId: Int) {
    window.navigationBarColor = ContextCompat.getColor(this, colorResId)
}

fun Activity.setWindowLightStatusBar(windowLightStatusBar: Boolean) {
    if (Build.VERSION.SDK_INT >= 23) {
        window.decorView.systemUiVisibility = if (windowLightStatusBar) {
            window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}

fun Activity.setWindowLightNavigationBar(windowLightNavigationBar: Boolean) {
    if (Build.VERSION.SDK_INT >= 26) {
        window.decorView.systemUiVisibility = if (windowLightNavigationBar) {
            window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            window.decorView.systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }
}

val Activity.toolbar
    get() = javaClass.getAnnotation(Toolbar::class.java)?.value?.let {
        findViewById<androidx.appcompat.widget.Toolbar>(it)
    }

fun Activity.hideSoftInputFromWindow() {
    currentFocus?.let {
        val manager = ContextCompat.getSystemService(this, InputMethodManager::class.java)
        manager?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

fun Activity.showToast(@StringRes messageResId: Int) =
    Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()