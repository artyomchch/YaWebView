package kozlov.artyom.yawebview.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kozlov.artyom.yawebview.utils.DataStoreRepository

class MyWebViewClient(private val context: Context) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, webResource: WebResourceRequest): Boolean {


//        when (webResource.url.toString()) {
//            in  MAPS_LINK ->  openNewApp(context, MAPS_APP)
//            in WEATHER_LINK -> openNewApp(context, WEATHER_APP)
//            else -> view?.loadUrl(webResource.url.toString())
//        }

        if (webResource.url.toString().contains(MAPS_LINK)) {
            openNewApp(context, MAPS_APP)
        } else if (webResource.url.toString().contains(WEATHER_LINK)) {
            openNewApp(context, WEATHER_APP)
        } else {
            view?.loadUrl(webResource.url.toString())
        }

        Log.d("TAG", webResource.url.toString())
        saveLink(webResource.url.toString())


        return super.shouldOverrideUrlLoading(view, webResource)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        //  progressBar.visibility = View.GONE
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        // errorAnim.visibility = View.GONE
    }

    @SuppressLint("WebViewClientOnReceivedSslError")
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
        handler?.proceed()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {

        if (error?.errorCode == ERROR_HOST_LOOKUP) {
            view?.loadUrl("about:blank")
        }

        super.onReceivedError(view, request, error)
    }

    private fun openNewApp(context: Context, packageName: String) {
        var intent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
        Log.d("TAG", "openNewApp: $intent")
        if (intent == null) {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$packageName")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun saveLink(url: String){
       CoroutineScope(Dispatchers.IO).launch {
           DataStoreRepository(context).saveToDataStore(url)
       }
    }

    companion object {
        private const val MAPS_LINK = "https://yandex.ru/maps"
        private const val MAPS_APP = "ru.yandex.yandexmaps"
        private const val WEATHER_LINK = "https://yandex.ru/pogoda"
        private const val WEATHER_APP = "ru.yandex.weatherplugin"
    }


}