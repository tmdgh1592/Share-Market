package com.app.buna.sharingmarket.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.adapter.ProductRecyclerAdapter
import com.app.buna.sharingmarket.databinding.ActivityMainBinding
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.google.android.material.internal.NavigationMenu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import io.github.yavski.fabspeeddial.FabSpeedDial
import org.koin.android.ext.android.get

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private val vm: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(get(), this))
            .get(MainViewModel::class.java)
    }
    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: TabLayout

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

        with(binding) {
            this?.productRecyclerView?.adapter = ProductRecyclerAdapter(vm.productItems, vm)
            this?.productRecyclerView?.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    /*view 초기화*/
    fun initView() {

        // * 툴바 관련
        toolbar = binding?.toolBar!!.also { setSupportActionBar(it) } // 액션바 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기

        // * 탭 레이아웃 관련
        tabLayout = binding?.mainTabLayout!!.apply {
            addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    val position = tab?.position

                    when(position) {
                        // 0 : 홈
                        0 -> {

                        }

                        // 1 : 카테고리
                        1 -> {

                        }

                        // 2 : 채팅
                        2 -> {

                        }

                        // 3 : 나무
                        3 -> {

                        }

                        // 4 : MY (개인 정보 설정)
                        4 -> {

                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }
            })
        }

        /* Fab 관련 */
        val speedDialView = binding?.mainFab
        speedDialView?.setMenuListener(object: FabSpeedDial.MenuListener{
            override fun onPrepareMenu(p0: NavigationMenu?): Boolean {
                return true
            }

            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
                when(menuItem?.itemId) {
                    R.id.action_write -> { // fab 무료나눔 버튼 클릭시
                        Snackbar.make(toolbar,"무료나눔",Snackbar.LENGTH_SHORT).show()
                    }
                    R.id.action_shopping -> { // fab 쇼핑버튼 버튼 클릭시
                        Snackbar.make(toolbar,"쇼핑하기",Snackbar.LENGTH_SHORT).show()
                    }
                }
                return false
            }

            override fun onMenuClosed() {
                return
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity_tool_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_category -> { // Toolbar 카테고리 버튼 클릭
                Snackbar.make(toolbar,"Account 카테고리 pressed",Snackbar.LENGTH_SHORT).show()
            }
            R.id.action_search -> { // Toolbar 검색 버튼 클릭
                Snackbar.make(toolbar,"Account 검색 pressed",Snackbar.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}