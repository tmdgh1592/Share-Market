package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.model.tree.TreeItem
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TreeViewModel(application: Application) : AndroidViewModel(application){
    var myTreeItem: TreeItem? = null // 트리 코인 모델
    val treeCoin = MutableLiveData<Int>() // 트리 코인 개수

    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TreeViewModel(application) as T
        }
    }

    fun getNickname(): String{
        return Firebase.auth.currentUser?.displayName ?: "나눔꾼"
    }

    fun getMyTree(callback: (TreeItem?) -> Unit) {
        FirebaseRepository.instance.getMyTreeItem(Firebase.auth.uid!!, callback)
    }

    // 트리코인을 count만큼 지급해주는 함수
    fun addTreeCoin(count: Int, complete: () -> Unit) {
        myTreeItem?.hasCoinCount = myTreeItem?.hasCoinCount?.plus(count)!!
        // 기존 코인 개수에 count만큼 더해서 갱신
        myTreeItem?.let { FirebaseRepository.instance.setMyTreeItem(Firebase.auth.uid!!, it) {
            // 코인 지급 버튼을 클릭했다고 갱신
            myTreeItem?.isClicked = true
            FirebaseRepository.instance.setClicked(myTreeItem!!, complete)
            treeCoin.postValue(count + treeCoin.value!!)
        } }

    }

    fun isClickedTree(): Boolean {
        return if (myTreeItem != null) {
            myTreeItem!!.isClicked
        } else {
            true
        }
    }

    // 트리 코인을 기부하는 경우
    fun giveTreeCoin(callback: (Int) -> Unit) {
        if (myTreeItem != null && treeCoin.value!! > 0) {
            FirebaseRepository.instance.giveCoin(myTreeItem!!, callback)
            treeCoin.postValue(0) // 트리 코인을 모두 기부하는 것이기 때문에 0으로 설정
            myTreeItem?.giveSeedCount = myTreeItem?.giveSeedCount?.plus(myTreeItem?.hasCoinCount!!)!!
            myTreeItem?.hasCoinCount = 0
        } else { // 기부할 수 있는 트리 코인이 없는 경우
            callback(0)
        }
    }

    // 씨앗을 많이 심은 Top5 프로필 가져오기
    fun getTreeProfile(callback: (ArrayList<String?>) -> Unit) {
        FirebaseRepository.instance.getTopGiveUserProfile(callback)
    }

}