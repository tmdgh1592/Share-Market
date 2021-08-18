package com.app.buna.sharemarket.koin

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val myModule = module(override = true) {
    single {
        Application()
    }
}

