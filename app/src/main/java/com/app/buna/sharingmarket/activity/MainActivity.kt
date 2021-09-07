package com.app.buna.sharingmarket.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.databinding.ActivityMainBinding
import com.app.buna.sharingmarket.fragment.main.MainCategoryFragment
import com.app.buna.sharingmarket.fragment.main.MainChatFragment
import com.app.buna.sharingmarket.fragment.main.MainHomeFragment
import com.app.buna.sharingmarket.fragment.main.MainMyFragment
import com.app.buna.sharingmarket.repository.Local.PreferenceUtil
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.get

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val vm: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(get(), this))
            .get(MainViewModel::class.java)
    }
    lateinit var tabLayout: TabLayout
    private var regTime = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initView()

        if (NetworkStatus.isConnectedInternet(this) && PreferenceUtil.getInt(this, "push") == 0) {
            vm.registerPushToken()
        }
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
        replaceFragment(MainHomeFragment.instance) // Home Fragment

        // * 탭 레이아웃 관련
        tabLayout = binding?.mainTabLayout!!.apply {
            addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab?.position) {
                        // 0 : 홈
                        0 -> replaceFragment(MainHomeFragment.instance)
                        // 1 : 카테고리
                        1 -> replaceFragment(MainCategoryFragment.instance)
                        // 2 : 채팅
                        2 -> replaceFragment(MainChatFragment.instance)
                        // 3 : MY (개인 정보 설정)
                        3 -> replaceFragment(MainMyFragment.instance)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    when(tab?.position) {
                        // 0 : 홈
                        0 -> replaceFragment(MainHomeFragment.instance)
                        // 1 : 카테고리
                        1 -> replaceFragment(MainCategoryFragment.instance)
                        // 2 : 채팅
                        2 -> replaceFragment(MainChatFragment.instance)
                        // 3 : MY (개인 정보 설정)
                        3 -> replaceFragment(MainMyFragment.instance)
                    }
                }
            })

        }
    }

    // 프래그먼트 전환하는 메소드
    fun replaceFragment(fragment: Fragment) =
        // tabLayout에서 클릭한 fragment 실행
        supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, fragment).commit()


    @SuppressLint("LongLogTag")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        when(resultCode) {
            REQUEST_CODE.DELETE_BOARD_CODE_FROM_MAIN -> { // 게시글을 삭제 했다면
                Log.d("MainActivity -> onActivityResult", "replace MainHomeFragment")
                //replaceFragment(MainHomeFragment()) // 게시글을 새로 불러오기 위해 HomeFragment 다시 실행
            } REQUEST_CODE.API_COMPLETED_FINISH -> {// 다음 주소에서 주소 선택했을 때 :: AddressApiWebView
                var jibun: String? = data?.getStringExtra("jibun")
                Log.d("Main", jibun)
                if (jibun != null && jibun != "") {

                }
            }
        }
            
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