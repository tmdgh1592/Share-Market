package com.app.buna.sharingmarket.fragment.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.activity.WriteActivity
import com.app.buna.sharingmarket.adapter.ProductRecyclerAdapter
import com.app.buna.sharingmarket.callbacks.IFirebaseGetStorageDataCallback
import com.app.buna.sharingmarket.databinding.FragmentMainHomeBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.google.android.material.internal.NavigationMenu
import com.google.android.material.snackbar.Snackbar
import io.github.yavski.fabspeeddial.FabSpeedDial
import org.koin.android.ext.android.get

class MainHomeFragment(val category: String = "all") : Fragment() {

    private var binding: FragmentMainHomeBinding? = null
    private val vm: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(get(), requireContext()))
            .get(MainViewModel::class.java)
    }
    private lateinit var toolbar: Toolbar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = vm
        }
        initView()
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    fun initView() {
        with(binding) {
            this?.productRecyclerView?.adapter = ProductRecyclerAdapter(vm, requireContext())
            this?.productRecyclerView?.layoutManager = LinearLayoutManager(requireContext())

            vm?.getProductData(category, object : IFirebaseGetStorageDataCallback {
                override fun complete(data: ArrayList<ProductItem>) {
                    (binding?.productRecyclerView?.adapter as ProductRecyclerAdapter).updateData(data)
                    vm?.productItems.value = (data)
                }
            })

        }


        // * 툴바 관련
        if (category == "all") { // 모두 보여주는 경우엔 Home Toolbar를 보여줌
            setHasOptionsMenu(true)
            toolbar = binding?.toolBar!!.also { (requireActivity() as MainActivity).setSupportActionBar(it) } // 액션바 지정
            (requireActivity() as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // 레이아웃에서 타이틀 직접 만들었으므로 이건 False
            binding?.mainFab?.visibility = View.VISIBLE // Fab버튼 보여주기
        } else { // 카테고리에서 선택한 경우에는 toolbar의 text를 category로 변경해준다.
            binding?.toolbarTitleText?.text = category // Toolbar 제목을 '카테고리명'으로 설정
            binding?.mainFab?.visibility = View.GONE // Fab버튼 감추기
        }


        /* Fab 버튼 관련 */
        val speedDialView = binding?.mainFab
        speedDialView?.setMenuListener(object : FabSpeedDial.MenuListener {
            override fun onPrepareMenu(p0: NavigationMenu?): Boolean {
                return true
            }

            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
                when (menuItem?.itemId) {
                    R.id.action_write -> { // fab 무료나눔 버튼 클릭시
                        // 게시글 작성(WriteActivity) 실행을 위한 Intent
                        val intent = Intent(context, WriteActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        (requireActivity() as MainActivity).startActivityForResult(intent, REQUEST_CODE.DELETE_BOARD_CODE_FROM_MAIN) // 게시글 작성 액티비티 실행
                    }
                    R.id.action_exchange -> { // fab 쇼핑버튼 버튼 클릭시
                        Snackbar.make(toolbar, "쇼핑하기", Snackbar.LENGTH_SHORT).show()
                    }
                }
                return false
            }

            override fun onMenuClosed() {
                return
            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_tool_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        /* 툴바 메뉴 선택 관련 */
        when (item.itemId) {
            R.id.action_category -> { // Toolbar 카테고리 버튼 클릭
                (requireActivity() as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame_layout, MainCategoryFragment.instance).commit() // 카테고리 Fragment로 이동
                (requireActivity() as MainActivity).tabLayout.getTabAt(1)?.select() // 탭도 같이 움직일 수 있도록 select() 호출
            }
            R.id.action_search -> { // Toolbar 검색 버튼 클릭
                Snackbar.make(toolbar, "Account 검색 pressed", Snackbar.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        val instance = MainHomeFragment()
    }


}