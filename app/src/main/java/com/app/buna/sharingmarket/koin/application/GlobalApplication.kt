package com.app.buna.sharingmarket.koin.application

import android.app.Application
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.koin.myModule
import com.kakao.sdk.common.KakaoSdk
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        /*Koin*/
        startKoin {
            androidLogger()
            androidContext(this@GlobalApplication)
            modules(myModule)
        }

    }
}