package com.app.buna.sharingmarket.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.activity.WriteActivity
import com.app.buna.sharingmarket.adapter.ProductRecyclerAdapter
import com.app.buna.sharingmarket.databinding.FragmentMainHomeBinding
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.google.android.material.internal.NavigationMenu
import com.google.android.material.snackbar.Snackbar
import io.github.yavski.fabspeeddial.FabSpeedDial
import org.koin.android.ext.android.get

class MainHomeFragment : Fragment() {

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
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        with(binding) {
            this?.productRecyclerView?.adapter = ProductRecyclerAdapter(vm.productItems, vm)
            this?.productRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
        }

        
        // * 툴바 관련
        setHasOptionsMenu(true)
        toolbar = binding?.toolBar!!.also { (requireActivity() as MainActivity).setSupportActionBar(it) } // 액션바 지정
        (requireActivity() as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // 레이아웃에서 타이틀 직접 만들었으므로 이건 False
        /*toolbar.setTitleTextAppearance(requireContext(), R.style.titleTextStyle) // 타이틀 font 지정*/


        /* Fab 버튼 관련 */
        val speedDialView = binding?.mainFab
        speedDialView?.setMenuListener(object: FabSpeedDial.MenuListener{
            override fun onPrepareMenu(p0: NavigationMenu?): Boolean {
                return true
            }

            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
                when(menuItem?.itemId) {
                    R.id.action_write -> { // fab 무료나눔 버튼 클릭시
                        // 게시글 작성(WriteActivity) 실행을 위한 Intent
                        val intent = Intent(context, WriteActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        startActivity(intent) // 게시글 작성 액티비티 실행
                    }
                    R.id.action_shopping -> { // fab 쇼핑버튼 버튼 클릭시
                        Snackbar.make(toolbar,"쇼핑하기", Snackbar.LENGTH_SHORT).show()
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

    companion object {
        val instacne = MainHomeFragment()
    }



}