package com.drugstore.app.webview

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.navigation.navArgs
import com.drugstore.R
import com.drugstore.app.createConfigurationContext
import com.drugstore.app.updateConfiguration
import com.drugstore.app.validateOrientation
import com.drugstore.databinding.ActivityWebViewBinding
import com.kasprov.android.core.activity.*

class WebViewActivity : BaseActivity() {

    private val args: WebViewActivityArgs by navArgs()
    val binding by viewDataBindings { ActivityWebViewBinding.inflate(layoutInflater) }

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

        binding.wvWebView.apply {
            webViewClient = AppWebViewClient(args.request.additionalHttpHeaders)
            webChromeClient = AppWebChromeClient(binding.pbWebView)
            loadUrl(args.request.url, args.request.additionalHttpHeaders)
        }
    }

    override fun onSetContentView() = setContentView(binding.root)

    override fun onResume() {
        super.onResume()
        binding.wvWebView.onResume()
    }

    override fun onBackPressed() {
        if (binding.wvWebView.canGoBack()) {
            binding.wvWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.wvWebView.onPause()
    }
}

private class AppWebViewClient(
    private val headers: Map<String, String>
) : WebViewClient() {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        view.loadUrl(request.url.toString(), headers)
        return true
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url, headers)
        return true
    }
}

private class AppWebChromeClient(
    private val progressBar: ProgressBar
) : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        progressBar.visibility = if (newProgress in 1..99) VISIBLE else GONE
        progressBar.progress = newProgress
    }
}