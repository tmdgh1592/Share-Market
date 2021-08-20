package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.model.items.LocationItem
import com.app.buna.sharingmarket.model.items.ProductItem

class MainViewModel(application: Application, val context: Context) : AndroidViewModel(application) {
    class Factory(val application: Application, val context: Context) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application, context) as T
        }
    }

    val productItems = MutableLiveData<List<ProductItem>>()
    val productList = ArrayList<ProductItem>()

    init {
        productList.add(ProductItem("1", "","Test1","","행신동","1분 전", ArrayList(),"", "", 10))
        productList.add(ProductItem("2", "","Test2","","행신동","1일 전",ArrayList(),"", "",5))
        productList.add(ProductItem("3","","Test3","","행신동","5분 전",ArrayList(),"", "",0))

        productItems.value = productList
    }

}