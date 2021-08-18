package com.app.buna.sharemarket.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import com.app.buna.sharemarket.R
import com.app.buna.sharemarket.utils.PreferenceUtil

class AddressApiWebView : AppCompatActivity() {

    private lateinit var context: Context

    companion object {
        const val ADDRESS_REQUEST_CODE = 2928
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_api_web_view)

        context = this
        val webView = findViewById<WebView>(R.id.web_view)

        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(KaKaoJavaScriptInterface(), "Android")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                webView.loadUrl("javascript:execKakaoPostcode();")
            }
        }

        // Kakao에서 https를 허용하지 않아서 https -> http로 바꿔야 동작함 (중요!!)
        webView.loadUrl("http://tmdgh1592.dothome.co.kr/daum.html")
    }

    inner class KaKaoJavaScriptInterface {

        @JavascriptInterface
        fun processDATA(address: String?) {
            Intent().apply {

                // address -> ex) 07728, 서울 강서구 까치산로 4 (하이트맨션)

                val streetNum = address?.split(',')?.get(0) // 도로 주소
                val myAddress = address?.split(',')?.get(1)?.trimStart() // 도로명

                streetNum?.let { PreferenceUtil.putString(context, "streetNum", it) }
                myAddress?.let { PreferenceUtil.putString(context, "address", it) }
                Log.d("AddressApiWebView", streetNum!!) // 07728
                Log.d("AddressApiWebView", myAddress!!) // 서울 강서구 까치산로 4 (하이트맨션)
            }
            finish()
        }
    }
}