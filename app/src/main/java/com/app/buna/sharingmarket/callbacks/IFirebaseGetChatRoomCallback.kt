package com.app.buna.sharingmarket.callbacks

import com.app.buna.sharingmarket.model.items.ChatRoom

interface IFirebaseGetChatRoomCallback {
    fun complete(chatRoomList: ArrayList<ChatRoom>)
}