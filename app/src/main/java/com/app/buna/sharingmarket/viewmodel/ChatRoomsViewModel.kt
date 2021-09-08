package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.activity.ChatActivity
import com.app.buna.sharingmarket.callbacks.IFirebaseGetChatRoomCallback
import com.app.buna.sharingmarket.model.items.chat.ChatModel
import com.app.buna.sharingmarket.model.items.chat.ChatUserModel
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatRoomsViewModel(application: Application, val context: Context) : AndroidViewModel(application) {
    class Factory(val application: Application, val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChatRoomsViewModel(application, context) as T
        }
    }

    // 채팅할 대상들의 채팅 정보 리스트
    var chatModels = ArrayList<ChatModel>()
    // 채팅할 대상들의 개인 정보 리스트
    var destUserModel = ArrayList<ChatUserModel>()

    fun getChatRoomList(callback: IFirebaseGetChatRoomCallback) {
        FirebaseRepository.instance.getChatModelList(callback)
    }

    fun findDestUid(users: HashMap<String, Boolean>): String? {
        // haspmap에서 본인 uid가 아닌 key값을 가져옴
        val filteredMap = users.filterKeys { !it.contains(getMyUid()!!) }
        return filteredMap.keys.first() // 상대방 uid를 return함.
    }

    // User의 데이터 Model을 반환
    fun getUserModel(uid: String, complete: (ChatUserModel) -> Unit) {
        FirebaseRepository.instance.getUserModel(uid, complete)
    }

    fun getMyUid() = Firebase.auth.uid

    fun goToChatRoom(view: View, destUserModel: ChatUserModel) {
        val intent = Intent(view.context, ChatActivity::class.java).apply {
            putExtra("userName", destUserModel.userName) // 채팅할 상대방 닉네임 전달 받음
            putExtra("profileImageUrl", destUserModel.profileImageUrl) // 채팅 상대방 프로필 Url 가져옴
            putExtra("destUid", destUserModel.uid) // 채팅할 상대방 Uid를 전달 받음
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        view.context.startActivity(intent)
    }

}