package com.app.buna.sharingmarket.service.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.ChatActivity
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        if (token != null) {
            val uid: String? = Firebase.auth.uid
            val tokenMap = mutableMapOf<String, Any>()

            tokenMap.put("pushtoken", token)
            FirebaseRepository.instance.registerToken(uid, tokenMap)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if(remoteMessage.notification != null) {
            sendNotification(remoteMessage.notification!!)
        }
    }

    private fun sendNotification(notification: RemoteMessage.Notification) {
        // 0. Pending Intent
        val notificationIntent = Intent(this, ChatActivity::class.java)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        // 1. 알림 메세지를 관리하는 notifcationManager 객체 추출
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = getNotifcationBuilder(notificationManager, "channel id", "first_channel")

        builder.setContentTitle(notification.title)
            .setContentText(notification.body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(100, builder.build())
    }

    private fun getNotifcationBuilder(notificationManager: NotificationManager, channelId: String, channelName: CharSequence): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(this, channelId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2. NotificationChannel 객체 생성 (첫번째 인자 : 관리 id, 두번째 인자 : 사용자에게 보여줄 채널 이름)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)

            // 3. 알림 메세지를 관리하는 객체에 노티피케이션 채널 등록
            notificationManager.createNotificationChannel(channel)
            builder.setSmallIcon(R.drawable.app_icon)
            return builder
        } else {
            builder.setSmallIcon(R.drawable.app_icon)
            return builder
        }
    }
}