package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.callbacks.FirebaseRepositoryCallback
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth

class WriteViewModel(application: Application) : AndroidViewModel(application) {

    var category: String? = null
    var isGive: Boolean? = null

    class Factory(val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return WriteViewModel(application) as T
        }
    }

    fun uploadProduct(item: ProductItem, callback: FirebaseRepositoryCallback) {
        FirebaseRepository.instance.uploadBoard(item, callback)
    }

    fun getUserInfo(key: String): String? { // 유저 DB에 담긴 데이터를 key값으로 가져옴
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        Log.d("WrtieViewModel", uid)
        return FirebaseRepository.instance.getUserInfo(uid, key)
    }

    fun getUserName(): String? { // 유저의 닉네임 가져오기
        return FirebaseAuth.getInstance().currentUser?.displayName
    }
}