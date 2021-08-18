package com.app.buna.sharemarket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.app.buna.sharemarket.R
import com.app.buna.sharemarket.fragment.FirstInitialFragment

class InitialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        replaceFragment(FirstInitialFragment()) // 처음 켰을 때 맨 처음 프래그먼트 화면 띄우기

    }

    // 프래그먼트 전환하는 메소드
    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        
        fragmentTransaction
            .setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out) // 프래그먼트 전환시 보여질 애니메이션 설정
            .replace(R.id.initial_frame_layout, fragment).commit() // 해당 프래그먼트 실행
    }
}