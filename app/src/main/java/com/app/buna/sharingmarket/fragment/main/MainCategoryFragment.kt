package com.app.buna.sharingmarket.fragment.main

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.adapter.CategoryGridAdapter
import com.app.buna.sharingmarket.databinding.FragmentMainCategoryBinding
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import org.koin.android.ext.android.get

class MainCategoryFragment : Fragment() {

    private var binding: FragmentMainCategoryBinding? = null
    private val vm: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(get(), requireContext()))
            .get(MainViewModel::class.java)
    }
    private lateinit var toolbar: Toolbar
    private lateinit var gridViewAdapter: CategoryGridAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater).apply {
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

        gridViewAdapter = CategoryGridAdapter(requireContext(), vm.getCategoryList())
        binding?.categoryGridView?.apply {
            adapter = gridViewAdapter
            onItemClickListener = AdapterView.OnItemClickListener { parent, v, position, id ->
                // 선택한 카테고리 가져오기
                val category = vm?.getCategoryList().get(position).title

                // 메인 Fragment로 이동하면서 선택한 category 데이터만 가져올 수 있도록 값 전달
                (requireActivity() as MainActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                    .replace(R.id.main_frame_layout, MainHomeFragment(category)).commit()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home_tool_bar, menu)
    }


    companion object {
        val instance = MainCategoryFragment()
    }


}