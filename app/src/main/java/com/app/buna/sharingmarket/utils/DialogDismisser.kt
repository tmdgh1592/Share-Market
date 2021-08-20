package com.app.buna.sharingmarket.utils

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.util.Log


class DialogDismisser {
    fun dismiss(d: DialogInterface?) {
        if (d == null) return
        try {
            if (d is AlertDialog) {
                if ((d as AlertDialog).isShowing()) (d as AlertDialog).dismiss()
                return
            }
            if (d is ProgressDialog) {
                if ((d as ProgressDialog).isShowing()) (d as ProgressDialog).dismiss()
                return
            }
            if (d is Dialog) {
                if ((d as Dialog).isShowing()) (d as Dialog).dismiss()
                return
            }
        } catch (e: Exception) {
            Log.e("dissmiss error", e.toString())
        }
    }

    fun dismiss(d1: DialogInterface?, d2: DialogInterface?) {
        dismiss(d1)
        dismiss(d2)
    }
}