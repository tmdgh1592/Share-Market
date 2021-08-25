package com.app.buna.sharingmarket.utils

import android.content.Context
import android.os.IBinder
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

class KeyboardUtil {

    companion object {
        // 키보드 닫기
        fun hideKeyBoard(context: Context, token: IBinder) {
            (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                token,
                0
            )
        }
    }
}