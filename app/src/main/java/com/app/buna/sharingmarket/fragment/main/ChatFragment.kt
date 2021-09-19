package com.app.buna.sharingmarket.fragment.main

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.adapter.ChatRoomRecyclerAdapter
import com.app.buna.sharingmarket.callbacks.IFirebaseGetChatRoomCallback
import com.app.buna.sharingmarket.databinding.FragmentMainChatBinding
import com.app.buna.sharingmarket.model.chat.ChatRoomModel
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.app.buna.sharingmarket.viewmodel.ChatRoomsViewModel
import org.koin.android.ext.android.get

class ChatFragment : Fragment() {

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
        // 인터넷에 연결되어 있지 않으면 연결해달라는 문구 출력
        if(!NetworkStatus.isConnectedInternet(requireContext())) {
            FancyToastUtil(requireContext()).showRed(getString(R.string.internet_check))
        }

        // * 툴바 관련
        toolbar =
            binding?.toolBar!!.also { (requireActivity() as MainActivity).setSupportActionBar(it) } // 액션바 지정
        (requireActivity() as MainActivity).supportActionBar?.setDisplayShowTitleEnabled(false) // 타이틀 안보이게 하기

        binding?.chatRoomRecyclerView?.apply {
            adapter = ChatRoomRecyclerAdapter(vm)
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
        }

        // 채팅방을 가져오는 함수
        // Firebase의 addValueEventListener가 있기 때문에 채팅방의 내용이 갱신될때마다 가져옴 (Observer pattern)
        vm.getChatRoomList(object : IFirebaseGetChatRoomCallback {
            override fun complete(chatRoomModels: ArrayList<ChatRoomModel>) {
                var count = 0 // Firebase는 비동기로 데이터를 가져오기 때문에 count 변수가 가져올 전체 데이터 개수가 되면 그 때 adapter를 update해줘야 함.
                vm.destUserModel.clear() // 기존 상대방 유저들 모델 리스트 삭제
                vm.chatModels.clear()
                vm.chatModels.addAll(chatRoomModels.sortedByDescending { it.lastTimestamp }) // chatRoomList -> 채팅방에 사용할 데이터 리스트 && 가장 최근 채팅을 가져오기 위해 lastTimeStamp 순으로 오름차순 정렬
                //vm.chatModels.sortByDescending { it.lastTimestamp }
                vm.chatModels.forEach { chatModel ->
                    val destUid = vm.findDestUid(chatModel.users) // 채팅 상대방 uid
                    if (destUid != null) {
                        vm.getUserModel(destUid) { userModel -> // 상대방 Uid를 찾아서 이 Uid로 해당 유저 정보 가져오기
                            vm.destUserModel.add(userModel)
                            //count++ // 데이터 카운트 1씩 // 증가
                            if (++count == chatRoomModels.size){ // 가져올 데이터를 모두 가져왔다면
                                // Recycler View에게 알려줌
                                (binding?.chatRoomRecyclerView?.adapter as ChatRoomRecyclerAdapter).update(vm.chatModels.distinct(), vm.destUserModel.distinct())
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home_tool_bar, menu)
    }


    companion object {
        val instance = ChatFragment()
    }


}