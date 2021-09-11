package com.app.buna.sharingmarket.callbacks

import com.app.buna.sharingmarket.model.items.chat.ChatRoomModel

interface IFirebaseGetChatRoomCallback {
    fun complete(chatRoomRoomList: ArrayList<ChatRoomModel>)
}