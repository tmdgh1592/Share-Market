package com.app.buna.sharingmarket.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.adapter.ProductRecyclerAdapter
import com.app.buna.sharingmarket.databinding.ActivityMainBinding
import com.app.buna.sharingmarket.databinding.FragmentMainCategoryBinding
import com.app.buna.sharingmarket.databinding.FragmentMainHomeBinding
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.google.android.material.internal.NavigationMenu
import com.google.android.material.snackbar.Snackbar
import io.github.yavski.fabspeeddial.FabSpeedDial
import org.koin.android.ext.android.get

class MainChatFragment : Fragment() {

    private var binding: FragmentMainCategoryBinding? = null
    private val vm: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(get(), requireContext()))
            .get(MainViewModel::class.java)
    }
    private lateinit var toolbar: Toolbar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater, container, false).apply {
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
        // * 툴바 관련
        toolbar =
            binding?.toolBar!!.also { (requireActivity() as MainActivity).setSupportActionBar(it) } // 액션바 지정
        (requireActivity() as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main_home_tool_bar, menu)
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
        val instacne = MainChatFragment()
    }


}