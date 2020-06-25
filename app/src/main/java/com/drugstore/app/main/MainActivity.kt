package com.drugstore.app.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.drugstore.R
import com.drugstore.app.core.navigation.setupWithNavController
import com.drugstore.app.createConfigurationContext
import com.drugstore.app.login.LoginActivity
import com.drugstore.app.updateConfiguration
import com.drugstore.app.validateOrientation
import com.drugstore.databinding.ActivityMainBinding
import com.kasprov.android.core.activity.*
import com.kasprov.android.core.config.activity.Toolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Toolbar(R.id.tb_main)
class MainActivity : BaseActivity() {

    val binding by viewDataBindings { ActivityMainBinding.inflate(layoutInflater) }
    val model: MainActivityVM by viewModels()

    private var currentNavController: LiveData<NavController>? = null
    override val navController get() = currentNavController?.value

    override fun attachBaseContext(newBase: Context) =
        super.attachBaseContext(newBase.createConfigurationContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        validateOrientation()
        updateConfiguration()
        setTheme(R.style.Theme)
        setFullscreenSystemUiVisibility()
        super.onCreate(savedInstanceState)
        setStatusBarColorResource(R.color.statusBarColor_light)
        setNavigationBarColorResource(R.color.navigationBarColor_light)
        setWindowLightStatusBar(true)
        setWindowLightNavigationBar(true)

        if (model.shouldNavigateToLogin()) {
            navigateToLogin()
        } else {
            if (savedInstanceState == null) launchNavigation()
            observeNonNull(model.navigateToLogin) { navigateToLogin() }
        }
    }

    override fun onSetContentView() = setContentView(binding.root)

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        launchNavigation()
    }

    internal fun launchNavigation() {
        currentNavController = binding.bnvMain.setupWithNavController(
            navGraphIds = listOf(R.navigation.categories, R.navigation.orders, R.navigation.options),
            fragmentManager = supportFragmentManager,
            containerId = R.id.fcv_main,
            intent = intent
        ).apply {
            observeNonNull(this) { it.setupWithNavigationViews() }
        }
    }

    internal fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra(NAME_EXTRAS, intent.extras)
        }
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        model.onStart()
    }

    companion object {
        const val NAME_EXTRAS = "extras"
    }
}