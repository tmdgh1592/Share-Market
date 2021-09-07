package com.app.buna.sharingmarket.notification.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.ChatActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val CHANNEL_ID = "1000"
    val CHANNEL_DESCRIPTION = "푸시 알림을 전달받기 위한 채널입니다."

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("MyFirebaseMessagingService", "message received")
        createNotificationChannel(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        sendNotification(remoteMessage)
    }


    /*private fun sendRegistrationToServer(token: String?) {
        if (token != null) {
            val uid: String? = Firebase.auth.uid
            val tokenMap = mutableMapOf<String, Any>()

            tokenMap["pushtoken"] = token
            FirebaseRepository.instance.registerToken(uid, tokenMap)
        }
    }*/

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2. NotificationChannel 객체 생성 (첫번째 인자 : 관리 id, 두번째 인자 : 사용자에게 보여줄 채널 이름)
            val channel = NotificationChannel(CHANNEL_ID, "push", NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)
            channel.description = CHANNEL_DESCRIPTION

            // 3. 알림 메세지를 관리하는 객체에 노티피케이션 채널 등록
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun sendNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        val notificationIntent = Intent(this, ChatActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(this).notify(1, notificationBuilder.build())
    }
}