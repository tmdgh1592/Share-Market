package com.app.buna.sharingmarket.fragment.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.adapter.ChatRoomRecyclerAdapter
import com.app.buna.sharingmarket.callbacks.IFirebaseGetChatRoomCallback
import com.app.buna.sharingmarket.databinding.FragmentMainChatBinding
import com.app.buna.sharingmarket.model.items.ChatRoom
import com.app.buna.sharingmarket.viewmodel.ChatRoomViewModel
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.get

class MainChatFragment : Fragment() {

    private var binding: FragmentMainChatBinding? = null
    private val vm: ChatRoomViewModel by lazy {
        ViewModelProvider(this, ChatRoomViewModel.Factory(get(), requireContext()))
            .get(ChatRoomViewModel::class.java)
    }
    private lateinit var toolbar: Toolbar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainChatBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = vm
        }
        initView()
        return binding?.root
    }


    fun initView() {
        // * 툴바 관련
        toolbar = binding?.toolBar!!.also { (requireActivity() as MainActivity).setSupportActionBar(it) } // 액션바 지정
        (requireActivity() as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기

        binding?.chatRoomRecyclerView?.apply {
            adapter = ChatRoomRecyclerAdapter()
            layoutManager = LinearLayoutManager(requireContext())
        }
        // 테스트 데이터
        val list = ArrayList<ChatRoom>()
        list.add(ChatRoom("야 롤 언제 들어와 tlqkffusdk","https://firebasestorage.googleapis.com/v0/b/sharing-market.appspot.com/o/profiles%2Falsrudgns.png?alt=media&token=1eee39c3-7fe9-4505-a16c-c1cb7cbaa23a", "1", "민경훈", "ㅇㅁㄴㅇ", 1L))
        (binding?.chatRoomRecyclerView?.adapter as ChatRoomRecyclerAdapter).update(list)

        vm.getChatRoomList(object : IFirebaseGetChatRoomCallback {
            override fun complete(chatRoomList: ArrayList<ChatRoom>) {
                (binding?.chatRoomRecyclerView?.adapter as ChatRoomRecyclerAdapter).update(chatRoomList)
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home_tool_bar, menu)
    }


    companion object {
        val instance = MainChatFragment()
    }


}