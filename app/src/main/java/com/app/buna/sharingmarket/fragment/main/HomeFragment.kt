package com.app.buna.sharingmarket.fragment.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.WriteType
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.activity.SearchActivity
import com.app.buna.sharingmarket.activity.WriteActivity
import com.app.buna.sharingmarket.adapter.BoardRecyclerAdapter
import com.app.buna.sharingmarket.callbacks.IFirebaseGetStoreDataCallback
import com.app.buna.sharingmarket.databinding.FragmentMainHomeBinding
import com.app.buna.sharingmarket.model.BoardItem
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.google.android.material.internal.NavigationMenu
import io.github.yavski.fabspeeddial.FabSpeedDial
import org.koin.android.ext.android.get

class HomeFragment(val category: String = "all") : Fragment() {

    private var keyword: String? = null

    constructor(category: String = "all", keyword: String?) : this(category) {
        this.keyword = keyword
    }


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
        binding = FragmentMainHomeBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = vm
        }
        initView()
        return binding?.root
    }

    fun initView() {
        with(binding) {
            val boardRecyclerAdapter = BoardRecyclerAdapter(vm, requireContext())
            boardRecyclerAdapter.setHasStableIds(true)
            this?.productRecyclerView?.apply {
                adapter = boardRecyclerAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            // RecyclerView에 사용할 데이터를 불러온다.
            loadData()

        }

        // 새로고침
        binding?.swipeRefreshLayout?.apply {

            setColorSchemeResources(R.color.app_green)

            // 새로고침을 하는 경우
            setOnRefreshListener {
                // 데이터 새로 불러오기
                loadData()
            }
        }


        // * 툴바 관련
        if (category == "all") { // 모두 보여주는 경우엔 Home Toolbar를 보여줌
            setHasOptionsMenu(true)
            toolbar =
                binding?.toolBar!!.also { (requireActivity() as MainActivity).setSupportActionBar(it) } // 액션바 지정
            (requireActivity() as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // 레이아웃에서 타이틀 직접 만들었으므로 이건 False
            binding?.mainFab?.visibility = View.VISIBLE // Fab버튼 보여주기
        } else { // 카테고리에서 선택한 경우에는 toolbar의 text를 category로 변경해준다.
            binding?.toolbarTitleText?.text = category // Toolbar 제목을 '카테고리명'으로 설정
            binding?.mainFab?.visibility = View.GONE // Fab버튼 감추기
        }


        /* Fab 버튼 관련 */

        binding?.mainFab?.setMenuListener(object : FabSpeedDial.MenuListener {
            override fun onPrepareMenu(p0: NavigationMenu?): Boolean {
                return true
            }

            override fun onMenuItemSelected(menuItem: MenuItem?): Boolean {
                when (menuItem?.itemId) {
                    R.id.action_write -> { // fab 무료나눔 버튼 클릭시
                        // 게시글 작성(WriteActivity) 실행을 위한 Intent
                        val intent = Intent(context, WriteActivity::class.java).apply {
                            putExtra("write_type", WriteType.SHARE)
                            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        (requireActivity() as MainActivity).startActivity(intent) // 게시글 작성 액티비티 실행
                    }
                    R.id.action_exchange -> { // fab 쇼핑버튼 버튼 클릭시
                        // 게시글 작성(WriteActivity) 실행을 위한 Intent
                        val intent = Intent(context, WriteActivity::class.java).apply {
                            putExtra("write_type", WriteType.EXCHANGE)
                            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        }
                        (requireActivity() as MainActivity).startActivity(intent) // 게시글 작성 액티비티 실행
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
                    .replace(R.id.main_frame_layout, CategoryFragment.instance)
                    .commit() // 카테고리 Fragment로 이동
                (requireActivity() as MainActivity).tabLayout.getTabAt(1)
                    ?.select() // 탭도 같이 움직일 수 있도록 select() 호출
            }
            R.id.action_search -> { // Toolbar 검색 버튼 클릭
                // 검색 화면을 보여주기 위한 다이얼로그
                val searchIntent = Intent(requireContext(), SearchActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                (requireContext() as MainActivity).startActivityForResult(
                    searchIntent,
                    REQUEST_CODE.SEARCH_BOARD_CODE
                )
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Firebase로부터 keyword가 속한 게시글을 불러온다
    // 또는 전체 게시글을 불러온다.
    private fun loadData() {
        // 키워드 검색을 한 경우
        if (keyword != null) {
            Log.d("search keyword", keyword)

            // 게시글의 제목에 keyword가 들어가 있는 게시글들을 가져옴
            vm?.getBoardByKeyword(keyword!!, object : IFirebaseGetStoreDataCallback {
                override fun complete(data: ArrayList<BoardItem>) {
                    if (data.size == 0) { // 키워드로 찾으려는 결과가 없다면
                        /* 리사이클러뷰 대신에 No Result View를 보여줌 */
                        binding?.noResultView?.visibility = View.VISIBLE
                        binding?.productRecyclerView?.visibility = View.GONE
                    } else { // 키워드로 찾으려는 결과가 하나라도 있다면
                        /* 리사이클러뷰를 보여줌 */
                        binding?.noResultView?.visibility = View.GONE
                        binding?.productRecyclerView?.visibility = View.VISIBLE

                        val boardList = ArrayList<BoardItem>().apply {
                            // 나눔 현황 확인하는 뷰 맨 앞에 추가
                            add(BoardItem())
                            addAll(data)
                        }

                        (binding?.productRecyclerView?.adapter as BoardRecyclerAdapter).updateData(
                            boardList
                        )
                        vm?.productItems.postValue(boardList)

                        // 새로고침을 하는 경우이면
                        if (binding?.swipeRefreshLayout?.isRefreshing!!) {
                            // Refresh 아이콘 사라지게 하기
                            binding?.swipeRefreshLayout?.isRefreshing = false
                        }
                    }
                }
            })
        } else { // 키워드 검색없이 HomeFragment에 들어온 경우
            vm?.getProductData(category, object : IFirebaseGetStoreDataCallback {
                override fun complete(data: ArrayList<BoardItem>) {
                    val boardList = ArrayList<BoardItem>().apply {
                        // 나눔 현황 확인하는 뷰 맨 앞에 추가
                        add(BoardItem())
                        addAll(data)
                    }
                    (binding?.productRecyclerView?.adapter as BoardRecyclerAdapter).updateData(
                        boardList
                    )
                    vm?.productItems.value = (boardList)

                    // 새로고침을 하는 경우이면
                    if (binding?.swipeRefreshLayout?.isRefreshing!!) {
                        // Refresh 아이콘 사라지게 하기
                        binding?.swipeRefreshLayout?.isRefreshing = false
                    }
                }
            })
        }
    }

    companion object {
        fun instance() = HomeFragment()
        fun instance(keyword: String?) = HomeFragment(keyword = keyword)
    }


}