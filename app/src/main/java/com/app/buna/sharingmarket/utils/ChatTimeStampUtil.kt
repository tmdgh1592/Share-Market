package com.app.buna.sharingmarket.utils

import java.text.SimpleDateFormat
import java.util.*

class ChatTimeStampUtil {
    companion object {
        fun getStamp(timeStamp: Long): String {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.KOREA)
            val timeZone = TimeZone.getTimeZone("Asia/Seoul")
            sdf.timeZone = timeZone
            return sdf.format(timeStamp)
        }
    }
}