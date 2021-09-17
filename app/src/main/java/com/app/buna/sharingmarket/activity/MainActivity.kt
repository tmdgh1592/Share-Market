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
import com.app.buna.sharingmarket.fragment.main.*
import com.app.buna.sharingmarket.repository.Local.PreferenceUtil
import com.app.buna.sharingmarket.utils.FancyToastUtil
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
        replaceFragment(HomeFragment.instance()) // Home Fragment

        // * 탭 레이아웃 관련
        tabLayout = binding?.mainTabLayout!!.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        // 0 : 홈
                        0 -> replaceFragment(HomeFragment.instance())
                        // 1 : 카테고리
                        1 -> replaceFragment(CategoryFragment.instance)
                        // 2 : 나무 심기 캠페인
                        2 -> replaceFragment(TreeFragment.instance)
                        // 3 : 채팅
                        3 -> replaceFragment(ChatFragment.instance)
                        // 4 : MY (개인 정보 설정)
                        4 -> replaceFragment(MyFragment.instance)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        // 0 : 홈
                        0 -> replaceFragment(HomeFragment.instance())
                        // 1 : 카테고리
                        1 -> replaceFragment(CategoryFragment.instance)
                        // 2 : 나무 심기 캠페인
                        2 -> replaceFragment(TreeFragment.instance)
                        // 3 : 채팅
                        3 -> replaceFragment(ChatFragment.instance)
                        // 4 : MY (개인 정보 설정)
                        4 -> replaceFragment(MyFragment.instance)
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

        when (requestCode) {
            REQUEST_CODE.API_COMPLETED_FINISH -> {// 다음 주소에서 주소 선택했을 때 :: AddressApiWebView
                var jibun: String? = data?.getStringExtra("jibun")
                Log.d("Main", jibun)
                if (jibun != null && jibun != "") {

                }
            }
            REQUEST_CODE.SEARCH_BOARD_CODE -> {
                val searchKeyword = data?.getStringExtra("keyword")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, HomeFragment.instance(searchKeyword))
                    .commit()
            }
            REQUEST_CODE.REFRESH_MAIN_HOME_FRAGMENT_CODE, REQUEST_CODE.UPDATE_BOARD_CODE -> { // 상품 정보 삭제, 수정, 나눔완료 등으로 인한 화면 갱신
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, HomeFragment.instance()).commit()
                if (data != null && !data.getBooleanExtra(
                        "refresh",
                        true
                    )
                ) { // SelectUserActivity에서부터 전달받은 상대방 데이터
                    startActivity(data) // 해당 데이터를 통해 ChatActivity로 이동
                }
            }
            REQUEST_CODE.REFRESH_MAIN_CHAT_FRAGMENT_CODE -> { // 유효하지 않은 채팅방으로 이동하여 채팅방이 삭제될 시 refresh
                FancyToastUtil(this).showRed(getString(R.string.dest_is_null))
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, ChatFragment.instance).commit()
            }
        }

    }

    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        if ((curTime - regTime) > 2000) {
            Toast.makeText(this, getString(R.string.back_press), Toast.LENGTH_SHORT).show()
            regTime = curTime
            return
        }
        finish()
    }
}