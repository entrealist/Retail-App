package com.drugstore.app.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.drugstore.R
import com.drugstore.app.createConfigurationContext
import com.drugstore.app.main.MainActivity
import com.drugstore.app.updateConfiguration
import com.drugstore.app.validateOrientation
import com.drugstore.databinding.ActivityLoginBinding
import com.kasprov.android.core.activity.BaseActivity
import com.kasprov.android.core.activity.observeNonNull
import com.kasprov.android.core.activity.setFullscreenSystemUiVisibility
import com.kasprov.android.core.activity.viewDataBindings
import com.kasprov.android.core.config.activity.NavHostFragment
import com.kasprov.android.core.config.activity.Toolbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@NavHostFragment(R.id.fcv_login)
@Toolbar(R.id.tb_login)
class LoginActivity : BaseActivity() {

    val binding by viewDataBindings { ActivityLoginBinding.inflate(layoutInflater) }
    val model: LoginActivityVM by viewModels()

    override fun attachBaseContext(newBase: Context) =
        super.attachBaseContext(newBase.createConfigurationContext())

    override fun onCreate(savedInstanceState: Bundle?) {
        validateOrientation()
        updateConfiguration()
        setTheme(R.style.Theme)
        setFullscreenSystemUiVisibility()
        super.onCreate(savedInstanceState)

        if (model.shouldNavigateToMain()) {
            navigateToMain()
        } else {
            observeNonNull(model.navigateToMain) { navigateToMain() }
        }
    }

    override fun onSetContentView() = setContentView(binding.root)

    internal fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            intent.getBundleExtra(MainActivity.NAME_EXTRAS)?.let { putExtras(it) }
        }
        startActivity(intent)
        finish()
    }
}