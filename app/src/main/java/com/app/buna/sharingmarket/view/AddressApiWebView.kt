package com.app.buna.sharingmarket.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.app.buna.sharingmarket.CONST
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.repository.PreferenceUtil

class AddressApiWebView : AppCompatActivity() {

    private lateinit var context: Context

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
        fun processDATA(address: String?) { // address -> ex) 07728, 서울 강서구 까치산로 4 (하이트맨션)

            val streetNum = address?.split(',')?.get(0) // 도로 주소
            val myAddress = address?.split(',')?.get(1)?.trimStart() // 도로명
            val jibun = address?.split(',')?.get(2)?.trimStart() // 지번

            streetNum?.let { PreferenceUtil.putString(context, "streetNum", it) }
            myAddress?.let { PreferenceUtil.putString(context, "address", it) }
            jibun?.let { PreferenceUtil.putString(context, "jibun", it) }

            Log.d("AddressApiWebView", streetNum!!) // 07728
            Log.d("AddressApiWebView", myAddress!!) // 서울 강서구 까치산로 4 (하이트맨션)
            Log.d("AddressApiWebView", jibun!!) // 경기 고양시 덕양구 행신동 633-36

            setResult(CONST.API_COMPLETED_FINISH)
            finish() // 주소 선택시 액티비티 종료
        }
    }
}