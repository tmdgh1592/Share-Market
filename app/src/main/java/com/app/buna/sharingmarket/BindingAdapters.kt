package com.app.buna.sharingmarket

import androidx.databinding.BindingAdapter
import com.google.android.gms.common.SignInButton

object BindingAdapters {
    @BindingAdapter("android:onClick")
    @JvmStatic
    fun bindSignInClick(button: SignInButton, method: () -> Unit) {
        button.setOnClickListener { method.invoke() }
    }
}

