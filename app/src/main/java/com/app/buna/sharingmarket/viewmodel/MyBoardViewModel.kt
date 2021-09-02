package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.app.buna.sharingmarket.callbacks.IFirebaseGetStoreDataCallback
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository

class MyBoardViewModel(application: Application, val context: Context) : AndroidViewModel(application){

    class Factory(val application: Application, val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MyBoardViewModel(application, context) as T
        }
    }
    var myBoardItems = MutableLiveData<ArrayList<ProductItem>>()
    var selectedItemPosition = 0

    // 내가 쓴 글 불러오기
    fun getMyBoards(callback: IFirebaseGetStoreDataCallback) {
        FirebaseRepository.instance.getProductData(callback)
    }

    fun getMyHearts(callback: IFirebaseGetStoreDataCallback){
        FirebaseRepository.instance.getLikeProductData(callback)
    }


}