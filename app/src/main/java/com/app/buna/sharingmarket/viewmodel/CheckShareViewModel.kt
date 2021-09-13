package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository

class CheckShareViewModel(application: Application) : AndroidViewModel(application) {
    class Factory(val application: Application) : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CheckShareViewModel(application) as T
        }
    }

    // 실시간으로 현재 게시글의 전체 개수를 가져온다.
    fun getBoardTotalCount(callback: (Int) -> Unit) {
        FirebaseRepository.instance.getBoardTotalCount(callback)
    }
}