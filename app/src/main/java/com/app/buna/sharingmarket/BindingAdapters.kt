package com.app.buna.sharingmarket

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.cardview.widget.CardView
import com.app.buna.sharingmarket.activity.CheckShareActivity
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.bumptech.glide.Glide
import com.google.android.gms.common.SignInButton
import de.hdodenhof.circleimageview.CircleImageView

object BindingAdapters {

    // 구글 로그인 버튼
    @BindingAdapter("android:onClick")
    @JvmStatic
    fun bindSignInClick(button: SignInButton, method: () -> Unit) {
        button.setOnClickListener { method.invoke() }
    }

    // 하트 개수 텍스트뷰
    @BindingAdapter("android:heart_count")
    @JvmStatic
    fun bindSignInClick(textView: TextView, count: Int) {
        when (count) {
            0 -> textView.visibility = View.GONE // 개수가 0개면 안보이게 함
            else -> {
                textView.visibility = View.VISIBLE
                textView.setText(Integer.toString(count))
            }
        }
    }

    // 하트 이미지 뷰
    @BindingAdapter("android:heart_visible")
    @JvmStatic
    fun bindSignInClick(imageView: ImageView, count: Int) {
        when (count) {
            0 -> imageView.visibility = View.GONE // 개수가 0개면 안보이게 함
            else -> {
                imageView.visibility = View.VISIBLE
            }
        }
    }


    @BindingAdapter("background_url")
    @JvmStatic
    fun bindImage(imageView: ImageView, url: String) {
        if (url != "null") {
            Glide.with(imageView.context).load(url).centerCrop().into(imageView)
        }
    }

    @BindingAdapter("background_res")
    @JvmStatic
    fun bindImage(imageView: ImageView, res: Int) {
        if (res != null) {
            Glide.with(imageView.context).load(res).fitCenter().into(imageView)
        }
    }

    @BindingAdapter("chat_profile_img")
    @JvmStatic
    fun bindChatProfileImage(imageView: CircleImageView, uri: String?) {
        if (uri != null && uri != "") {
            Glide.with(imageView.context).load(Uri.parse(uri)).error(R.drawable.default_profile)
                .fallback(R.drawable.default_profile).fitCenter().into(imageView)
        } else {
            Glide.with(imageView.context).load(R.drawable.default_profile).fitCenter()
                .into(imageView)
        }
    }

    @BindingAdapter("text_click")
    @JvmStatic
    fun bindClickChatBubble(layout: TextView, message: String) {
        layout.setOnClickListener { view ->
            val clipboardManager =
                view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("message", message)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(
                view.context,
                view.context.getString(R.string.clip_success),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}


