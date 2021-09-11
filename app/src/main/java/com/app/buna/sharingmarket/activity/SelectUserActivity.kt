package com.app.buna.sharingmarket.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.adapter.UserSelectRecyclerAdapter
import com.app.buna.sharingmarket.databinding.ActivitySelectUserBinding
import com.app.buna.sharingmarket.listners.FailType
import com.app.buna.sharingmarket.listners.ViewModelListner
import com.app.buna.sharingmarket.model.items.chat.ChatUserModel
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.viewmodel.UserSelectViewModel
import org.koin.android.ext.android.get

class SelectUserActivity : AppCompatActivity() {
    var binding: ActivitySelectUserBinding? = null
    val vm: UserSelectViewModel by lazy {
        ViewModelProvider(
            this,
            UserSelectViewModel.Factory(get())
        ).get(UserSelectViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView() // 뷰 초기화
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
        vm.getMyChatUsers { chatRoomUsers ->
            Log.d("size", chatRoomUsers.isNullOrEmpty().toString())
            vm.selectUserList = chatRoomUsers
            (binding?.userSelectRecyclerView?.adapter as UserSelectRecyclerAdapter).update(chatRoomUsers)

            // 채팅한 기록이 없으면 결과 없음 화면을 보여준다.
            if(chatRoomUsers.isNullOrEmpty()) {
                binding?.doneBtn?.visibility = View.GONE
                binding?.userSelectRecyclerView?.visibility = View.GONE
                binding?.noResultView?.visibility = View.VISIBLE
            }
        }

        // 선택한 유저가 바뀌었을 때 감지
        vm.selectedUserPos.observe(this, Observer {
            // recycler view 갱신
            (binding?.userSelectRecyclerView?.adapter as UserSelectRecyclerAdapter).notifyDataSetChanged()
        })

        binding?.doneBtn?.setOnClickListener {
            vm.clickDoneBtn(object : ViewModelListner {
                override fun onSuccess(data: Any?) {
                    if (data is ChatUserModel && data != null) {// 상대방 정보 모델
                        val intent =
                            Intent(this@SelectUserActivity, ChatActivity::class.java).apply {
                                putExtra("userName", data.userName) // 채팅할 상대방 닉네임 전달 받음
                                putExtra(
                                    "profileImageUrl",
                                    data.profileImageUrl
                                ) // 채팅 상대방 프로필 Url 가져옴
                                putExtra("destUid", data.uid)
                            }

                        startActivity(intent)
                        setResult(RESULT_OK)
                        finish()

                    }
                }

                override fun onFail(failType: Int) { // 클릭에 실패했을 때
                    if (failType == FailType.NO_SELECTED) { // 유저를 선택하지 않은 경우
                        FancyToastUtil(this@SelectUserActivity).showGreen(getString(R.string.no_selected_user))
                    }
                }
            })
        }
    }
}