package com.app.buna.sharingmarket.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.adapter.ChatRecyclerAdatper
import com.app.buna.sharingmarket.databinding.ActivityChatBinding
import com.app.buna.sharingmarket.model.items.chat.ChatUserModel
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
        // 툴바 사용
        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


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

    fun getPushState() {
        val destUid = viewModel.destChatModel?.uid!! // 상대방 uid

        // 상대방 uid를 통해 roomUid를 찾음
        viewModel.getChatRoomUid(destUid) { roomUid ->
            if(roomUid != null) { // 상대방과의 채팅방이 있다면
                // 상대방 uid를 통해 상대방의 push 허용 상태를 가져옴
                viewModel.canReceivePush(destUid) { result ->
                    viewModel.destPushState.postValue(result)
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        getPushState() // 상대방 push 상태를 가져옴

        viewModel.myPushState.observe(this, Observer { state ->
            val notiMenuItem: MenuItem = menu?.findItem(R.id.item_noti)!!
            if (state) { // 상대방이 푸시 알림을 받을 수 있는 조건이면
                notiMenuItem.icon = ContextCompat.getDrawable(this, R.drawable.icon_noti)
            } else { // 푸시알림을 받지 못하는 조건이면 (푸시알림 차단, 이미 채팅방에 있는 경우)
                notiMenuItem.icon = ContextCompat.getDrawable(this, R.drawable.icon_noti_block)
            }
        })

        // 아이콘 상태를 다시 확인할 수 있도록 postValue 호출
        viewModel.myPushState.postValue(viewModel.myPushState.value)

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_noti -> {
                if (viewModel.myPushState.value!!) { // 현재 푸시를 받는 상태인데 푸시를 차단하고 싶어서 버튼을 누른 경우
                    Toast.makeText(this, getString(R.string.push_block), Toast.LENGTH_LONG).show() // 푸시알림 차단 메세지 출력
                } else { // 현재 푸시를 차단한 상태인데 푸시를 받겠다고 버튼을 누른 경우
                    Toast.makeText(this, getString(R.string.push_access), Toast.LENGTH_LONG).show() // 푸시알림 승인 메세지 출력
                }
                viewModel.myPushState.postValue(viewModel.myPushState.value?.not())
            }
        }
        return true
    }


    override fun onResume() {
        // 홈버튼을 눌렀다가 다시 채팅방으로 돌아오면 푸시 알림을 받지 않음
        if (viewModel.chatRoomUid != null) {
            viewModel.setPushState(
                false,
                viewModel.chatRoomUid!!,
                viewModel.getUid()!!
            )
        }
        super.onResume()
    }

    override fun onUserLeaveHint() {
        // 채팅방에서 나갈 때 채팅수신 승인 상태라면
        if (viewModel.myPushState.value == true && viewModel.chatRoomUid != null) {
            // 홈버튼을 눌러 바탕화면으로 이동할 땐, 푸시알림을 받겠다고 재설정 (채팅방에 들어오면 채팅 수신을 받지 않기 때문에)
            viewModel.setPushState(true, viewModel.chatRoomUid!!, viewModel.getUid()!!)
        }
        super.onUserLeaveHint()
    }

    override fun onDestroy() {
        // 나갈 때 푸시 알림 상태 바꿈 (바로 서버에서 상태를 바꾸면 채팅방 내에서도 푸시알림이 전송됨.)
        viewModel.setPushState(viewModel.myPushState.value!!, viewModel.chatRoomUid!!, viewModel.getUid()!!)

        // 채팅방에서 나갈 때 채팅수신 승인 상태라면
        if (viewModel.myPushState.value == true && viewModel.chatRoomUid != null) {
            // 나갈땐 알림 받겠다고 재설정 (채팅방에 들어오면 채팅 수신을 받지 않기 때문에)
            viewModel.setPushState(true, viewModel.chatRoomUid!!, viewModel.getUid()!!)
        }
        super.onDestroy()
    }
}