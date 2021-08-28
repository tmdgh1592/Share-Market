package com.app.buna.sharingmarket.koin

import android.app.Application
import com.app.buna.sharingmarket.viewmodel.BoardViewModel
import com.facebook.CallbackManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val myModule = module(override = true) {
    single {
        Application()
    }
}

val viewModelsModules = module(override = true) {
    viewModel<BoardViewModel> {
        BoardViewModel(get(), androidContext())
    }
}

