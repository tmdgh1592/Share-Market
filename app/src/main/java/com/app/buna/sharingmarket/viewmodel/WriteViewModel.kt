package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.callbacks.FirebaseRepositoryCallback
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.repository.FirebaseRepository

class WriteViewModel(application: Application) : AndroidViewModel(application) {

    class Factory(val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return WriteViewModel(application) as T
        }
    }

    fun uploadProduct(callback: FirebaseRepositoryCallback) {
        FirebaseRepository.instance.uploadBoard(ProductItem("1", "","Test1","","행신동","1분 전", ArrayList(),"", "", 10, false), callback)
    }

}