package com.app.buna.sharingmarket.notification.notification

import android.content.Context
import android.util.Log
import com.app.buna.sharingmarket.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions.builder
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.installations.remote.InstallationResponse.builder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

// 채팅할 때 상대방 Token을 통해 Notification 데이터를 전달하는 클래스
class SendNotification {

    companion object {
        // Media type의 json 형식
        val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")!!

        // 푸시알림 데이터 전달 (regToken : 상대방 토큰)
        fun sendNotification(context: Context, regToken: String, title: String, message: String) {
            CoroutineScope(Dispatchers.Default).launch { // 코루틴을 통해 백그라운드에서 작업 수행
                try {
                    val client = OkHttpClient()
                    val messageJson = JSONObject() // "key -> message"
                    val dataJson = JSONObject() // "token(상대방)", "notification(title, message)"
                    val notiJson = JSONObject() // "title", "body"

                    notiJson.put("title", title)
                    notiJson.put("body", message)

                    dataJson.put("token", regToken)
                    dataJson.put("notification", notiJson)

                    messageJson.put("message", dataJson)
                    val body = RequestBody.create(JSON, messageJson.toString()) // Request에 필요한 Body생성

                    val request = Request.Builder()
                        .addHeader(
                            "Authorization",
                            "Bearer ${getAccessToken(context)}"
                        )
                        .url("https://fcm.googleapis.com/v1/projects/sharing-market/messages:send")
                        .post(body)
                        .build()

                    // response -> 400 : 잘못된 Json형식, 403 : 권한 없음
                    val response = client.newCall(request).execute()
                    Log.d("response code", response.code().toString())
                    Log.d("response message", response.message())

                } catch (e: Exception) {
                    Log.d("error", e.message)
                }
            }
        }

        private fun getAccessToken(context: Context): String {
            val stream =
                context.resources.openRawResource(R.raw.sharing_market) // Google Cloud Flatform의 key값을 json으로 받은 형태

            val googleCredentials = GoogleCredentials
                .fromStream(stream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform")

            googleCredentials.refreshIfExpired() // 토큰 최대 유지 시간 : 3600second(60분)
            val token = googleCredentials.accessToken.tokenValue
            return token
        }
    }

}