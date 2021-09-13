package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CheckShareViewModel(application: Application) : AndroidViewModel(application) {
    class Factory(val application: Application) : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CheckShareViewModel(application) as T
        }

    }
}