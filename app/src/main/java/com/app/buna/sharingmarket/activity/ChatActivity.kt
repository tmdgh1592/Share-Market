package com.app.buna.sharingmarket.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.adapter.ChatRecyclerAdatper
import com.app.buna.sharingmarket.databinding.ActivityChatBinding
import com.app.buna.sharingmarket.model.items.chat.UserModel
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
        viewModel.destChatModel = UserModel(
            intent.getStringExtra("userName"), // 채팅할 상대방 닉네임 전달 받음
            intent.getStringExtra("profileImageUrl"), // 채팅 상대방 프로필 Url 가져옴
            intent.getStringExtra("destUid") // 채팅할 상대방 Uid를 전달 받음
        )
        initView()
        // destChatModel에 대한 데이터를 가져왔으면 채팅방 유무 확인 후 생성
        viewModel.registerChatRoomUid(intent.getStringExtra("destUid")) {
            viewModel.getChatList { newChatList ->
                if (newChatList.isNotEmpty()) {
                    (binding.chatRecyclerView.adapter as ChatRecyclerAdatper).update(newChatList)
                    binding?.chatRecyclerView.smoothScrollToPosition((binding.chatRecyclerView.adapter as ChatRecyclerAdatper).itemCount - 1)
                }
            }
        }

    }

    fun initView() {
        // 채팅보내면 채팅 입력칸 비우기
        binding?.submitBtn.setOnClickListener {
            // 메세지를 과도하게 보내는 것을 방지하기 위함
            binding?.submitBtn.isClickable = false
            viewModel.sendMesage {
                // 메세지가 다 전송되면
                binding?.submitBtn.isClickable = true
            }
            binding?.chatEditTextView.text.clear()
            viewModel.message = ""
        }

        binding?.chatRecyclerView.apply {
            adapter = ChatRecyclerAdatper(viewModel.destChatModel!!)
            layoutManager = LinearLayoutManager(this@ChatActivity)
        }
    }
    

    override fun onDestroy() {
        viewModel
        super.onDestroy()
    }
}