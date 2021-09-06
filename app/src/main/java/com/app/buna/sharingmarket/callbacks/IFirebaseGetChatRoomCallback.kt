package com.app.buna.sharingmarket.callbacks

import com.app.buna.sharingmarket.model.items.chat.ChatModel

interface IFirebaseGetChatRoomCallback {
    fun complete(chatRoomList: ArrayList<ChatModel>)
}