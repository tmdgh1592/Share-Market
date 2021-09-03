package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.callbacks.IFirebaseGetChatRoomCallback
import com.app.buna.sharingmarket.model.items.chat.ChatRoom

class ChatRoomsViewModel(application: Application, val context: Context) : AndroidViewModel(application) {
    class Factory(val application: Application, val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChatRoomsViewModel(application, context) as T
        }
    }

    val chatRoomList = MutableLiveData<ArrayList<ChatRoom>>()

    fun getChatRoomList(callback: IFirebaseGetChatRoomCallback) {

    }

}