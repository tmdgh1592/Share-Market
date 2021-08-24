package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.activity.BoardActivity
import com.app.buna.sharingmarket.callbacks.FirebaseGetStorageDataCallback
import com.app.buna.sharingmarket.callbacks.FirebaseRepositoryCallback
import com.app.buna.sharingmarket.model.items.LocationItem
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.repository.FirebaseRepository

class MainViewModel(application: Application, val context: Context) :
    AndroidViewModel(application) {
    class Factory(val application: Application, val context: Context) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application, context) as T
        }
    }

    val productItems = MutableLiveData<ArrayList<ProductItem>>(ArrayList())


    // View Model의 product
    fun getProductData(callback: FirebaseGetStorageDataCallback) {
        FirebaseRepository.instance.getProductData(callback)
    }

    fun clickProduct(position: Int) {

        val intent = Intent(context, BoardActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra("product_item", productItems.value!!.get(position))
        context.startActivity(intent)
    }


}