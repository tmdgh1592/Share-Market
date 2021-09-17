package com.app.buna.sharingmarket.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.adapter.UserSelectRecyclerAdapter
import com.app.buna.sharingmarket.databinding.ActivitySelectUserBinding
import com.app.buna.sharingmarket.listners.FailType
import com.app.buna.sharingmarket.listners.ViewModelListner
import com.app.buna.sharingmarket.model.chat.ChatUserModel
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.viewmodel.SelectUserViewModel

class SelectUserActivity : AppCompatActivity() {
    var binding: ActivitySelectUserBinding? = null
    val vm: SelectUserViewModel by lazy {
        ViewModelProvider(
            this,
            SelectUserViewModel.Factory(application)
        ).get(SelectUserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView() // 뷰 초기화
        vm.boardTitle = intent.getStringExtra("board_title") // BoardActivity로부터 게시글 제목 전달받음
    }

    fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_user)
        binding?.lifecycleOwner = this
        binding?.viewModel = vm

        binding?.userSelectRecyclerView?.apply {
            this.layoutManager = LinearLayoutManager(this@SelectUserActivity)
            setHasFixedSize(true)
            adapter = UserSelectRecyclerAdapter(vm)
        }

        // 채팅했던 유저 리스트 가져와서 adapter에 전송
        // 추가로 해당 채팅방의 key값(Room uid)도 가져옴
        vm.getMyChatUsers { chatRoomUsers, roomUids ->
            vm.selectUserList = chatRoomUsers
            vm.roomUids.addAll(roomUids)
            (binding?.userSelectRecyclerView?.adapter as UserSelectRecyclerAdapter).update(
                chatRoomUsers
            )

            // 채팅한 기록이 있다면 리스트 화면을 보여준다.
            if (!chatRoomUsers.isNullOrEmpty()) {
                binding?.doneBtn?.run {
                    isEnabled = true // 버튼 활성화
                    setTextColor(ContextCompat.getColor(this@SelectUserActivity, R.color.app_green))
                }
                binding?.userSelectRecyclerView?.visibility = View.VISIBLE
                binding?.noResultView?.visibility = View.GONE
            }
        }

        // 선택한 유저가 바뀌었을 때 감지
        vm.selectedUserPos.observe(this, Observer {
            // recycler view 갱신
            (binding?.userSelectRecyclerView?.adapter as UserSelectRecyclerAdapter).notifyDataSetChanged()
        })

        binding?.doneBtn?.setOnClickListener {
            vm.clickDoneBtn(object : ViewModelListner { // 커스텀 리스너로 확인버튼을 눌렀다고 알려주고 결과를 전달받는다.
                override fun onSuccess(data: Any?) { // success
                    if (data is ChatUserModel) {// 상대방 정보 모델
                        // destModelIntent : 상대방 정보를 담은 인텐트
                        // setResult()를 통해 메인 액티비티까지 종료하면서 데이터를 가져가고, 메인액티비티에서 화면 갱신 후 ChatActivity로 이동
                        val destModelIntent = Intent(this@SelectUserActivity, ChatActivity::class.java).apply {
                                putExtra("userName", data.userName) // 채팅할 상대방 닉네임 전달 받음
                                putExtra(
                                    "profileImageUrl",
                                    data.profileImageUrl
                                ) // 채팅 상대방 프로필 Url 가져옴
                                putExtra("destUid", data.uid)
                            }

                        setResult(RESULT_OK, destModelIntent)
                        finish()

                    }
                }

                override fun onFail(failType: Int) { // 클릭에 실패했을 때
                    if (failType == FailType.NO_SELECTED) { // 유저를 선택하지 않은 경우
                        FancyToastUtil(this@SelectUserActivity).showGreen(getString(R.string.no_selected_user))
                    } else if (failType == FailType.INTERNET_STATE_ERROR) {
                        FancyToastUtil(this@SelectUserActivity).showRed(getString(R.string.internet_check))
                    }
                }
            })
        }
    }
}