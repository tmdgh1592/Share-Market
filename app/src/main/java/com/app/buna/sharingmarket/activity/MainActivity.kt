package com.app.buna.sharingmarket.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.ActivityMainBinding
import com.app.buna.sharingmarket.fragment.MainCategoryFragment
import com.app.buna.sharingmarket.fragment.MainChatFragment
import com.app.buna.sharingmarket.fragment.MainHomeFragment
import com.app.buna.sharingmarket.fragment.MainMyFragment
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.get

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val vm: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(get(), this))
            .get(MainViewModel::class.java)
    }
    private lateinit var tabLayout: TabLayout
    private var regTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initView()
    }

    /*Binding 초기화*/
    fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.lifecycleOwner = this
        binding?.viewModel = vm
    }

    /*view 초기화*/

    fun initView() {
        // 초기 실행 fragment
        replaceFragment(MainHomeFragment.instacne) // Home Fragment

        // * 탭 레이아웃 관련
        tabLayout = binding?.mainTabLayout!!.apply {
            addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val position = tab?.position

                    when(position) {
                        // 0 : 홈
                        0 -> {
                            replaceFragment(MainHomeFragment.instacne)
                        }

                        // 1 : 카테고리
                        1 -> {
                            replaceFragment(MainCategoryFragment.instacne)
                        }

                        // 2 : 채팅
                        2 -> {
                            replaceFragment(MainChatFragment.instacne)
                        }

                        // 3 : MY (개인 정보 설정)
                        3 -> {
                            replaceFragment(MainMyFragment.instacne)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
        }


    }

    // 프래그먼트 전환하는 메소드
    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        // tabLayout에서 클릭한 fragment 실행
        fragmentTransaction.replace(R.id.main_frame_layout, fragment).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        if((curTime - regTime) > 2000) {
            Toast.makeText(this, getString(R.string.back_press), Toast.LENGTH_SHORT).show()
            regTime = curTime
            return
        }
        finish()
    }
}