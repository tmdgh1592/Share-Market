package com.app.buna.sharingmarket.notification.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.buna.sharingmarket.Channel
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.Splash
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = Channel.CHANNEL_ID
    private val CHANNEL_DESCRIPTION = Channel.CHANNEL_DESC
    private val NOTI_ID = Channel.NOTI_ID

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @SuppressLint("LongLogTag")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("MyFirebaseMessagingService", "message received")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)
        sendNotification(notificationManager, remoteMessage)
    }

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

    private fun sendNotification(notificationManager: NotificationManager, remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title // 닉네임
        val message = remoteMessage.notification?.body // 메세지 내용
        val profileUri = remoteMessage.notification?.imageUrl // 프로필 Url

        // 푸시알림 클릭시 Activity 중복 실행을 막음
        val notificationIntent = Intent(this, Splash::class.java).apply {
            action = Intent.ACTION_MAIN // 태스크의 첫(메인) 액티비티로 지정한다
            addCategory(Intent.CATEGORY_LAUNCHER) // 액티비티가 어플리케이션의 런처에 첫 액티비티가 될 수 있다.
            flags = Intent.FLAG_ACTIVITY_NEW_TASK // 새로운 태스크를 생성해서 new task에 액티비티 스택이 다시 쌓인다.
        }
        
        // 메세지 클릭시 앱으로 이동하기 위한 PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            this,
            1000,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 노티피케이션 빌더
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setTicker(getString(R.string.push_ticker))
            .setSmallIcon(R.drawable.app_icon)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE or Notification.FLAG_AUTO_CANCEL)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setWhen(System.currentTimeMillis())
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setAutoCancel(true)

        // 커스텀 노티피케이션 레이아웃
        val customView = RemoteViews(packageName, R.layout.notification_layout).apply {
            setTextViewText(R.id.title_text_view, title) // 메세지 제목 (닉네임)
            setTextViewText(R.id.message_text_view, message) // 메세지 내용
            notificationBuilder.setCustomContentView(this) // 노티피케이션 커스텀 뷰 Set
        }

        // 상대방이 프로필을 설정한 상대라면
        if (profileUri != null) {
            val notificationTarget = NotificationTarget(
                this,
                R.id.notification_profile_image_view,
                customView,
                notificationBuilder.build(),
                NOTI_ID
            )

            // 보낸 사람 프로필 이미지
            Glide.with(this).asBitmap().error(R.drawable.default_profile)
                .fallback(R.drawable.default_profile).load(profileUri).circleCrop()
                .into(notificationTarget)
        }
        // 푸시가 쌓이지 않도록 기존에 보낸 푸시는 삭제
        notificationManager.cancel(NOTI_ID)
        // 새로운 푸시 발동
        NotificationManagerCompat.from(this).notify(NOTI_ID, notificationBuilder.build())
    }
}