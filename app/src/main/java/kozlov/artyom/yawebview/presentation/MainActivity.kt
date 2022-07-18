package kozlov.artyom.yawebview.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebSettings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kozlov.artyom.yawebview.databinding.ActivityMainBinding
import kozlov.artyom.yawebview.utils.observeOnce
import kozlov.artyom.yawebview.webview.MyWebViewClient


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        syncCookie()
        initWebView()
        launchWebView()
    }

    private fun launchWebView() {
        mainActivityViewModel.readFromDataStore.observeOnce(this) {
            binding.webView.loadUrl(it)
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        with(binding.webView.settings) {
            javaScriptEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            setSupportMultipleWindows(true)
            loadWithOverviewMode = true
            allowContentAccess = true
            domStorageEnabled = true
            setGeolocationEnabled(true)
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            useWideViewPort = true

        }
        binding.webView.webViewClient = MyWebViewClient(applicationContext)
    }


    @SuppressLint("ObsoleteSdkInt")
    private fun syncCookie() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptThirdPartyCookies(binding.webView, true)
        } else {
            CookieManager.getInstance().setAcceptCookie(true)
        }
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.webView.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }

    override fun onBackPressed() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            allerDialog()
        }
    }

    private fun allerDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ -> this.finish() }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}