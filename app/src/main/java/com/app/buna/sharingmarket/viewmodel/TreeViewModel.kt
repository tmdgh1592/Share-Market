package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.model.tree.TreeItem
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.app.buna.sharingmarket.repository.Local.PreferenceUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TreeViewModel(application: Application) : AndroidViewModel(application){
    lateinit var myTreeItem: TreeItem // 트리 코인 모델
    val treeCoin = MutableLiveData<Int>() // 트리 코인 개수

    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TreeViewModel(application) as T
        }
    }

    fun getMyTree(callback: (TreeItem?) -> Unit) {
        FirebaseRepository.instance.getMyTreeItem(Firebase.auth.uid!!, callback)
    }

    // 트리코인을 count만큼 지급해주는 함수
    fun addTreeCoin(count: Int, complete: () -> Unit) {
        myTreeItem.totalSeed += count
        // 기존 코인 개수에 count만큼 더해서 갱신
        FirebaseRepository.instance.setMyTreeItem(Firebase.auth.uid!!, myTreeItem) {
            // 코인 지급 버튼을 클릭했다고 갱신
            myTreeItem.isClicked = true
            FirebaseRepository.instance.setClicked(myTreeItem, complete)
            treeCoin.postValue(count + treeCoin.value!!)
        }
    }

    fun isClickedTree(): Boolean {
        return myTreeItem.isClicked
    }
}