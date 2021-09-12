package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.model.items.chat.ChatRoomModel
import com.app.buna.sharingmarket.model.items.chat.ChatUserModel
import com.app.buna.sharingmarket.notification.notification.SendNotification
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.app.buna.sharingmarket.repository.Local.PreferenceUtil
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
    var destPushState = MutableLiveData<Boolean>(true) // Firebase에 저장된 상대방 push 수신 상태
    var myPushState = MutableLiveData<Boolean>(true) // 내 push 수신 상태

    //상대방과 채팅한 기록이 있는지 확인 후 있으면 채팅방 uid 가져옴
    var chatRoomUid: String? = null

    fun sendMesage(
        complete: (firstChatRoomList: ArrayList<ChatRoomModel.Comment>?) -> Unit,
        success: (Boolean) -> Unit
    ) {
        if (message != null && message.trim() != "" && destChatModel != null) {
            //채팅방 유저 맵
            val users = HashMap<String, Boolean>().apply {
                put(Firebase.auth.uid.toString(), true)
                put(destChatModel!!.uid, true)
            }
            // 전송할 채팅 모델 생성
            val comment = ChatRoomModel.Comment(
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
            }

            // 상대방 유저 모델이 null이 아니고 상대방이 푸시 승인 상태라면
            if (destChatModel != null && destPushState.value!!) {
                FirebaseRepository.instance.getPushToken(destChatModel!!.uid, { destPushToken ->
                    if (destPushToken != null) {
                        sendPushGson(destPushToken, getNickname(), comment.message)
                    } else { // 사용자가 회원탈퇴하면 token이 null값이 됨

                    }
                }, success)
            }

        }
    }

    fun getNickname(): String {
        return PreferenceUtil.getString(context, "nickname")
    }

    fun registerChatRoomUid(destUid: String, callback: () -> Unit) {
        FirebaseRepository.instance.checkChatRoom(destUid) { roomUid ->
            getPushState(roomUid!!, getUid()!!) { state -> // 서버의 푸시 상태를 가져와서 아이콘 State에 적용
                myPushState.postValue(state)
                setPushState(false, roomUid!!, getUid()!!) // 방에 들어오면 푸시알림 받지 않음
            }
            chatRoomUid = roomUid // 채팅방 Uid 초기화
            callback() // 채팅을 불러오도록 callback 실행
        }
    }

    fun getChatRoomUid(destUid: String, callback: (String?) -> Unit) {
        FirebaseRepository.instance.checkChatRoom(destUid) { roomUid ->
            callback(roomUid)
        }
    }

    fun getChatList(complete: (ArrayList<ChatRoomModel.Comment>) -> Unit) {
        FirebaseRepository.instance.getComments(chatRoomUid, complete)
    }

    fun sendPushGson(destPushToken: String, nickname: String, message: String) {
        val myAuth = Firebase.auth.uid
        if (myAuth != null ) {
            FirebaseRepository.instance.getProfile(myAuth) { profileUrl ->
                SendNotification.sendNotification(
                    context,
                    destPushToken,
                    nickname,
                    message,
                    profileUrl
                )
            }
        }
    }

    fun removeChatRoom(complete: () -> Unit) {
        FirebaseRepository.instance.removeChatRoom(chatRoomUid, complete)
    }

    fun getUid() = Firebase.auth.uid

    fun canReceivePush(destUid: String, callback: (Boolean) -> Unit) {
        FirebaseRepository.instance.canReceivePush(chatRoomUid!!, destUid, callback)
    }

    fun togglePushState(uid: String, complete: (Boolean) -> Unit) {
        FirebaseRepository.instance.togglePushState(
            myPushState.value!!,
            chatRoomUid!!,
            uid,
            complete
        )
    }

    fun setPushState(state: Boolean, chatRoomUid: String, uid: String) {
        FirebaseRepository.instance.setPushState(state, chatRoomUid, uid)
    }

    fun getPushState(chatRoomUid: String, uid: String, complete: (Boolean) -> Unit) {
        FirebaseRepository.instance.getPushState(chatRoomUid, uid, complete)
    }

    // 기부금 영수증 신청하기 버튼 눌렀을 때 호출하는 메솓,
    // 전자 기부금 영수증 앱 설치로 이동
    fun applyGiveBill() {
        val homeTaxAppPacakge = "kr.go.nts.android"
        var homeTaxIntent: Intent

        if(isInstalled(homeTaxAppPacakge)) { // 홈택스 앱이 깔려있다면
            homeTaxIntent = context.packageManager.getLaunchIntentForPackage(homeTaxAppPacakge)!!
            homeTaxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        } else {
            homeTaxIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${homeTaxAppPacakge}"))
        }
        context.startActivity(homeTaxIntent)
    }

    fun isInstalled(packageName: String): Boolean {
        var isExist = false
        val pkgMgr: PackageManager = context.packageManager
        val mApps: List<ResolveInfo>
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0)
        try {
            for (i in mApps.indices) {
                if (mApps[i].activityInfo.packageName.startsWith(packageName)) {
                    isExist = true
                    break
                }
            }
        } catch (e: Exception) {
            isExist = false
        }
        return isExist
    }

}