package com.app.buna.sharingmarket.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.adapter.ChatRecyclerAdatper
import com.app.buna.sharingmarket.databinding.ActivityChatBinding
import com.app.buna.sharingmarket.model.items.chat.ChatUserModel
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.app.buna.sharingmarket.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import org.koin.android.ext.android.get

class ChatActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatBinding
    val viewModel: ChatViewModel by lazy {
        ViewModelProvider(this, ChatViewModel.Factory(get(), this)).get(ChatViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding?.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
        // View Model의 채팅할 상대방 정보 DTO 세팅
        viewModel.destChatModel = ChatUserModel(
            intent.getStringExtra("userName"), // 채팅할 상대방 닉네임 전달 받음
            intent.getStringExtra("profileImageUrl"), // 채팅 상대방 프로필 Url 가져옴
            intent.getStringExtra("destUid") // 채팅할 상대방 Uid를 전달 받음
        )
        initView()
        // destChatModel에 대한 데이터를 가져왔으면 채팅방 유무 확인 후 생성
        viewModel.registerChatRoomUid(intent.getStringExtra("destUid")) {
            viewModel.getChatList { newChatList ->
                if (newChatList.isNotEmpty()) {
                     // 리사이클러뷰에 새로운 채팅 데이터를 전달
                    (binding.chatRecyclerView.adapter as ChatRecyclerAdatper).update(newChatList)
                    // 채팅 스크롤 맨 밑으로 이동
                    binding?.chatRecyclerView.scrollToPosition((binding.chatRecyclerView.adapter as ChatRecyclerAdatper).itemCount-1)
                }
            }
        }
    }

    fun initView() {
        // 채팅보내면 채팅 입력칸 비우기
        binding?.submitBtn.setOnClickListener {
            if (NetworkStatus.isConnectedInternet(this)) { // 인터넷이 연결되어 있을 때만
                // 메세지를 과도하게 보내는 것을 방지하기 위함
                binding?.submitBtn.isClickable = false
                viewModel.sendMesage { firstChatList ->
                    if (firstChatList != null) {
                        // 맨 처음에 채팅을 보내면 해당 채팅 가져오도록 설정
                        (binding.chatRecyclerView.adapter as ChatRecyclerAdatper).update(
                            firstChatList
                        )
                    }
                    /*// 메세지가 다 전송되면 채팅전송 버튼 다시 활성화
                    binding?.submitBtn.isClickable = true*/
                }
                binding?.chatEditTextView.text.clear()
                viewModel.message = ""

                // 채팅전송 버튼 다시 활성화
                binding?.submitBtn.isClickable = true
            } else {
                // 인터넷 연결 실패 토스트 보여주기
                FancyToastUtil(this).showFail(getString(R.string.internet_check))
            }

            // 스크롤을 맨 아래로 이동
            binding?.chatRecyclerView.scrollToPosition((binding.chatRecyclerView.adapter as ChatRecyclerAdatper).itemCount - 1)
        }


        binding?.chatRecyclerView.apply {
            adapter = ChatRecyclerAdatper(viewModel.destChatModel!!).apply {
                setHasStableIds(true)
            }
            layoutManager = LinearLayoutManager(this@ChatActivity)
            setHasFixedSize(true)
        }

        // 채팅 맨 밑으로 이동
        binding?.chatRecyclerView.scrollToPosition((binding.chatRecyclerView.adapter as ChatRecyclerAdatper).itemCount-1)
    }


    override fun onDestroy() {
        viewModel
        super.onDestroy()
    }
}