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

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.initial_frame_layout, fragment).commit()
    }
}