package com.app.buna.sharingmarket.utils

import android.content.Context
import androidx.room.OnConflictStrategy.FAIL
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.repository.PreferenceUtil.getString
import com.shashank.sony.fancytoastlib.FancyToast

class FancyToastUtil(val context: Context) {

    fun showSuccess(text: String) {
        FancyToast.makeText(
            context,
            text,
            FancyToast.LENGTH_LONG,
            FancyToast.SUCCESS,
            R.drawable.app_icon,
            false
        ).show()
    }

    fun showFail(text: String) {
        FancyToast.makeText(
            context,
            text,
            FancyToast.LENGTH_LONG,
            FancyToast.ERROR,
            R.drawable.app_icon,
            false
        ).show()
    }
}