package com.app.buna.sharemarekt

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.app.buna.sharemarekt.view.LoginActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java) //로그인 액티비티 전환을 위한 인텐트(Intent)
            intent.addFlags((Intent.FLAG_ACTIVITY_NO_ANIMATION)) // 액티비티 띄울 때, 애니메이션 제거

            startActivity(intent)
            finish()
        }, CONST.SPLASH_DURATION) // 일정 시간 후에 화면 전환
    }
}