package com.app.buna.sharingmarket

import androidx.databinding.BindingAdapter
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.SignInButton

object BindingAdapters {

    // 구글 로그인 버튼
    @BindingAdapter("android:onClick")
    @JvmStatic
    fun bindSignInClick(button: SignInButton, method: () -> Unit) {
        button.setOnClickListener { method.invoke() }
    }

    /*@BindingAdapter("android:onClick")
    @JvmStatic
    fun bindingFBSignInClick(button: LoginButton, method: () -> Unit) {
        button.setOnClickListener { method.invoke() }
    }*/
}

