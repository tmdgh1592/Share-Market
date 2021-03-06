package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.Const
import com.app.buna.sharingmarket.callbacks.IFirebaseRepositoryCallback
import com.app.buna.sharingmarket.model.main.BoardItem
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth

class WriteViewModel(application: Application) : AndroidViewModel(application) {

    var category: String? = null
    var isGive: Boolean? = null
    var isExchange: Boolean = false
    var imagePaths = ArrayList<String>(Const.MAX_PHOTO_SIZE)
    var imagePathHash = HashMap<String, Boolean>()
    var imageCount: MutableLiveData<Int> = MutableLiveData(0)
    var fileNameForDelete = ArrayList<String>(Const.MAX_PHOTO_SIZE)

    class Factory(val application: Application): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return WriteViewModel(application) as T
        }
    }

    // 가져오는데 딜레이 있음 (코루틴 await 사용해야 함)
    fun getUserInfo(key: String): String? { // 유저 DB에 담긴 데이터를 key값으로 가져옴
        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        Log.d("WrtieViewModel", uid)
        return FirebaseRepository.instance.getUserInfo(uid, key)
    }

    fun getUserName(): String? { // 유저의 닉네임 가져오기
        return FirebaseAuth.getInstance().currentUser?.displayName
    }

    fun getUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }


    // 상품 정보 등록
    fun uploadProduct(item: BoardItem, callback: IFirebaseRepositoryCallback) {
        FirebaseRepository.instance.uploadBoard(item, callback)
    }

    // 상품 정보 등록 후 -> callback -> 이미지 Save
    fun saveProductImage(imgPath: ArrayList<String>, boardUid: String) {
        if(imgPath.size != 0) {
            FirebaseRepository.instance.saveProductImg(imgPath, boardUid, fileNameForDelete)
        }
    }

    // 상품 정보 업데이트
    fun updateProduct(item: BoardItem, callback: IFirebaseRepositoryCallback) {
        FirebaseRepository.instance.updateBoard(item, callback)
    }

    // 상품 정보 업데이트 후 -> callback -> 이미지 새로 Save
    fun updateProductImage(boardUid: String, fileNamesForDelete: ArrayList<String>) {

        FirebaseRepository.instance.updateProductImg(imagePathHash, boardUid, fileNamesForDelete)

    }
}