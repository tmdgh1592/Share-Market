package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.ChatActivity
import com.app.buna.sharingmarket.activity.SelectUserActivity
import com.app.buna.sharingmarket.listners.FailType
import com.app.buna.sharingmarket.listners.ViewModelListner
import com.app.buna.sharingmarket.model.items.chat.ChatUserModel
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.app.buna.sharingmarket.utils.FancyToastUtil

class UserSelectViewModel(application: Application) : AndroidViewModel(application) {
    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UserSelectViewModel(application) as T
        }
    }

    var selectUserList = ArrayList<ChatUserModel>() // 채팅했던 유저들을 가져온 리스트
    val selectedUserPos = MutableLiveData<Int>() // 현재 선택된 유저의 position

    // 본인이 채팅했던 유저들
    fun getMyChatUsers(complete: (ArrayList<ChatUserModel>) -> Unit) {
        FirebaseRepository.instance.getChatDestUsers(complete)
    }

    // 확인 버튼을 눌렀을 때 함수
    fun clickDoneBtn(listener: ViewModelListner) {

        // 선택한 유저가 없으면 확인 메세지 후에 함수 종료
        if (selectedUserPos.value == null) {
            listener.onFail(FailType.NO_SELECTED) // 실패 리스너 콜백
            return
        }

        // 선택한 유저에게 채팅 전송
        sendRequireGiveBill(selectUserList[selectedUserPos.value!!])
        // 성공 리스너 onSuccess 콜백 실행
        listener.onSuccess(selectUserList[selectedUserPos.value!!])
    }

    // 상대방에게 기부금 영수증을 요청하는 메세지 전송
    fun sendRequireGiveBill(destUser: ChatUserModel) {
        if (destUser != null) {
            
        }
    }


}