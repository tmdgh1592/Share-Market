package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.callbacks.IFirebaseGetStoreDataCallback
import com.app.buna.sharingmarket.model.BoardItem
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository

class MyBoardViewModel(application: Application, val context: Context) : AndroidViewModel(application){

    class Factory(val application: Application, val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MyBoardViewModel(application, context) as T
        }
    }
    var myBoardItems = MutableLiveData<ArrayList<BoardItem>>()
    var selectedItemPosition = 0

    // 내가 쓴 글 불러오기
    fun getMyBoards(callback: IFirebaseGetStoreDataCallback) {
        FirebaseRepository.instance.getBoardData(callback)
    }

    fun getMyHearts(callback: IFirebaseGetStoreDataCallback){
        FirebaseRepository.instance.getLikeProductData(callback)
    }


}