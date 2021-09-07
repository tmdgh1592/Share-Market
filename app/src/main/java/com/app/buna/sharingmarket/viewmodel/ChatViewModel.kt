package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.model.items.chat.ChatModel
import com.app.buna.sharingmarket.model.items.chat.ChatUserModel
import com.app.buna.sharingmarket.notification.notification.SendNotification
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ChatViewModel(application: Application, val context: Context) : AndroidViewModel(application) {
    class Factory(val application: Application, val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ChatViewModel(application, context) as T
        }
    }
    var destChatModel: ChatUserModel? = null // 채팅하는 상대 유저 데이터
    var message: String = "" // 전송할 채팅 바인딩 데이터

    //상대방과 채팅한 기록이 있는지 확인 후 있으면 채팅방 uid 가져옴
    var chatRoomUid: String? = null

    fun sendMesage(complete: (firstChatList: ArrayList<ChatModel.Comment>?) -> Unit) {
        if (message != null && message.trim() != "" && destChatModel != null) {
            //채팅방 유저 맵
            val users = HashMap<String, Boolean>().apply {
                put(Firebase.auth.uid.toString(), true)
                put(destChatModel!!.uid, true)
            }
            // 전송할 채팅 모델 생성
            val comment = ChatModel.Comment(
                Firebase.auth.uid.toString(),
                message.trimStart(),
                System.currentTimeMillis()
            )
            // message 전송
            FirebaseRepository.instance.sendMessage(chatRoomUid, users, comment) { roomUid ->
                // 전송 완료시 전송 완료 콜백
                if (roomUid != null) {
                    chatRoomUid = roomUid
                    FirebaseRepository.instance.getComments(chatRoomUid) { chatList ->
                        complete(chatList)
                    }
                }
                complete(null)
            }

            if (destChatModel != null) {
                FirebaseRepository.instance.getPushToken(destChatModel!!.uid) { destPushToken ->
                    sendPushGson(destPushToken, destChatModel!!.userName, comment.message)
                }
            }

        }
    }

    fun registerChatRoomUid(destUid: String, callback: () -> Unit) {
        FirebaseRepository.instance.checkChatRoom(destUid) { roomUid ->
            chatRoomUid = roomUid // 채팅방 Uid 초기화
            callback() // 채팅을 불러오도록 callback 실행
        }
    }

    fun getChatList(complete: (ArrayList<ChatModel.Comment>) -> Unit) {
        FirebaseRepository.instance.getComments(chatRoomUid, complete)
    }

    fun sendPushGson(destPushToken: String, nickname: String, message: String) {
        SendNotification.sendNotification(
            context,
            destPushToken,
            nickname,
            message
        )

    }

}