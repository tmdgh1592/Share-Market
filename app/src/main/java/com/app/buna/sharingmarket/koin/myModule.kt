package com.app.buna.sharingmarket.koin

import android.app.Application
import org.koin.dsl.module


val myModule = module(override = true) {
    single {
        Application()
    }
}

