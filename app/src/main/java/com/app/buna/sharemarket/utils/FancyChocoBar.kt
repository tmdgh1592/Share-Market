package com.app.buna.sharemarket.utils

import android.app.Activity
import com.pd.chocobar.ChocoBar

class FancyChocoBar(val activity: Activity) {

    fun showSnackBar(text: String) {
        ChocoBar.builder().setActivity(activity)
            .setText(text)
            .setDuration(ChocoBar.LENGTH_LONG)
            .green()  // in built green ChocoBar
            .show()
    }

    fun showAlertSnackBar(text: String) {
        ChocoBar.builder().setActivity(activity)
            .setText(text)
            .setDuration(ChocoBar.LENGTH_LONG)
            .red()  // in built green ChocoBar
            .show()
    }

    fun showOrangeSnackBar(text: String) {
        ChocoBar.builder().setActivity(activity)
            .setText(text)
            .setDuration(ChocoBar.LENGTH_LONG)
            .orange()  // in built green ChocoBar
            .show()
    }
}