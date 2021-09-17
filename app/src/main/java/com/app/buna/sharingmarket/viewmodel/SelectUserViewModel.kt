package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.AppHostPush
import com.app.buna.sharingmarket.CommentType
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.listners.FailType
import com.app.buna.sharingmarket.listners.ViewModelListner
import com.app.buna.sharingmarket.model.chat.ChatRoomModel
import com.app.buna.sharingmarket.model.chat.ChatUserModel
import com.app.buna.sharingmarket.notification.notification.SendNotification
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SelectUserViewModel(application: Application) : AndroidViewModel(application) {

    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SelectUserViewModel(application) as T
        }
    }

    var selectUserList = ArrayList<ChatUserModel>() // 채팅했던 유저들을 가져온 리스트
    val roomUids = ArrayList<String>() // 채팅방 Room uid
    val selectedUserPos = MutableLiveData<Int>() // 현재 선택된 유저의 position
    lateinit var boardTitle: String

    // 본인이 채팅했던 유저들
    fun getMyChatUsers(complete: (ArrayList<ChatUserModel>, ArrayList<String>) -> Unit) {
        FirebaseRepository.instance.getChatDestUsers(complete)
    }

    // 확인 버튼을 눌렀을 때 함수
    fun clickDoneBtn(listener: ViewModelListner) {
        if (NetworkStatus.isConnectedInternet(getApplication<Application>().applicationContext)) {

            // 선택한 유저가 없으면 확인 메세지 후에 함수 종료
            if (selectedUserPos.value == null) {
                listener.onFail(FailType.NO_SELECTED) // 실패 리스너 콜백
                return
            }

            // 선택한 유저에게 채팅 전송
            sendRequireGiveBill(selectUserList[selectedUserPos.value!!])
            // 성공 리스너 onSuccess 콜백 실행
            listener.onSuccess(selectUserList[selectedUserPos.value!!])
        } else {
            listener.onFail(FailType.INTERNET_STATE_ERROR)
        }
    }

    // 상대방에게 기부금 영수증을 요청하는 메세지 전송
    private fun sendRequireGiveBill(destUser: ChatUserModel) {

        val users = HashMap<String, Boolean>().apply {
            put(Firebase.auth.uid!!, true)
            put(destUser.uid, true)
        }
        val giveBillMessage = "${String.format(getApplication<Application>().resources.getString(R.string.require_give_bill_title), boardTitle)}" +
                "${getApplication<Application>().resources.getString(R.string.require_give_bill_message)}"

        FirebaseRepository.instance.sendMessage(
            roomUids[selectedUserPos.value!!],
            users,
            ChatRoomModel.Comment(
                uid = Firebase.auth.uid!!,
                message = giveBillMessage,
                timeStamp = System.currentTimeMillis(),
                commentType = CommentType.GIVE_BILL
            )
        )
        sendRequestBillPush()
    }

    // 푸시 메세지 보내기
    fun sendRequestBillPush() {
        val myAuth = Firebase.auth.uid
        val context = getApplication<Application>().applicationContext
        if (myAuth != null) {
            // 상대방의 Push Token을 가져와서, '기부금 영수증 요청 푸시 메세지 전송'
            FirebaseRepository.instance.getPushToken(selectUserList[selectedUserPos.value!!].uid, {regToken ->
                if (regToken != null) {
                    SendNotification.sendNotification(
                        context,
                        regToken,
                        context.getString(R.string.app_name),
                        context.getString(R.string.require_give_bill_notification),
                        AppHostPush.HOST_NOTIFICAITON // 보낸 사람 프로필을 AppHost(개발자)로 지정
                    )
                }
            }){}
        }
    }

}