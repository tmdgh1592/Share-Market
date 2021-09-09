package com.app.buna.sharingmarket.fragment.main

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.adapter.ChatRoomRecyclerAdapter
import com.app.buna.sharingmarket.callbacks.IFirebaseGetChatRoomCallback
import com.app.buna.sharingmarket.databinding.FragmentMainChatBinding
import com.app.buna.sharingmarket.model.items.chat.ChatModel
import com.app.buna.sharingmarket.viewmodel.ChatRoomsViewModel
import org.koin.android.ext.android.get

class MainChatFragment : Fragment() {

    private var binding: FragmentMainChatBinding? = null
    private val vm: ChatRoomsViewModel by lazy {
        ViewModelProvider(this, ChatRoomsViewModel.Factory(get(), requireContext()))
            .get(ChatRoomsViewModel::class.java)
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
        toolbar =
            binding?.toolBar!!.also { (requireActivity() as MainActivity).setSupportActionBar(it) } // 액션바 지정
        (requireActivity() as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기

        binding?.chatRoomRecyclerView?.apply {
            adapter = ChatRoomRecyclerAdapter(vm)
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
        }

        vm.getChatRoomList(object : IFirebaseGetChatRoomCallback {
            override fun complete(chatRoomList: ArrayList<ChatModel>) {
                vm.chatModels = chatRoomList
                var count = 0 // Firebase는 비동기로 데이터를 가져오기 때문에 count 변수가 가져올 전체 데이터 개수가 되면 그 때 adapter를 update해줘야 함.
                chatRoomList.forEach { chatModel ->
                    val destUid = vm.findDestUid(chatModel.users) // 채팅 상대방 uid

                    if (destUid != null) {
                        vm.getUserModel(destUid) { userModel -> // 상대방 Uid를 찾아서 이 Uid로 해당 유저 정보 가져오기
                            vm.destUserModel.add(userModel)
                            count++

                            if (count == chatRoomList.size){ // 가져올 데이터를 모두 가져왔다면
                                vm.destUserModelLiveData.postValue(vm.destUserModel)
                            }
                        }
                    }
                }
            }
        })

        vm.destUserModelLiveData.observe(viewLifecycleOwner, Observer {
            (binding?.chatRoomRecyclerView?.adapter as ChatRoomRecyclerAdapter).update(vm.chatModels, it)
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