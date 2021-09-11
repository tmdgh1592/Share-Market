package com.app.buna.sharingmarket.utils

import android.content.Context
import com.app.buna.sharingmarket.R
import com.shashank.sony.fancytoastlib.FancyToast

class FancyToastUtil(val context: Context) {

    // 보통 성공 문구를 보여줄 때 사용
    fun showGreen(text: String) {
        val toast = FancyToast.makeText(context, text, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, R.drawable.app_icon, false)
        toast.show()
    }

    // 보통 실패 문구를 보여줄 때 사용
    fun showRed(text: String) {
        FancyToast.makeText(context, text, FancyToast.LENGTH_LONG, FancyToast.ERROR, R.drawable.app_icon, false).show()
    }

    // 보통 경고 문구를 보여줄 때 사용
    fun showWarning(text: String) {
        FancyToast.makeText(context, text, FancyToast.LENGTH_LONG, FancyToast.WARNING, R.drawable.app_icon, false).show()
    }
}