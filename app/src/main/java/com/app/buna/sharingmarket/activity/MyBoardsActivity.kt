package com.app.buna.sharingmarket.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.adapter.MyBoardsRecyclerAdapter
import com.app.buna.sharingmarket.callbacks.IFirebaseGetStoreDataCallback
import com.app.buna.sharingmarket.databinding.ActivityMyBoardsBinding
import com.app.buna.sharingmarket.model.main.BoardItem
import com.app.buna.sharingmarket.viewmodel.MyBoardViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyBoardsActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyBoardsBinding
    val vm: MyBoardViewModel by lazy {
        ViewModelProvider(
            this,
            MyBoardViewModel.Factory(application, this)
        ).get(MyBoardViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_boards)
        binding.lifecycleOwner = this
        binding.viewModel = vm

        initView()
    }

    fun initView() {
        binding?.myBoardRecyclerView.adapter = MyBoardsRecyclerAdapter(vm, this, this)
        binding?.myBoardRecyclerView?.layoutManager = LinearLayoutManager(this)

        vm?.getMyBoards(object : IFirebaseGetStoreDataCallback {
            override fun complete(data: ArrayList<BoardItem>) {
                vm.myBoardItems.postValue(data) // viewmodel의 리스트 데이터들도 갱신

                if (data.size == 0) { // 데이터가 0개면 No Result View를 보여줌
                    binding?.myBoardRecyclerView.visibility = View.GONE
                    binding?.noResultView.visibility = View.VISIBLE
                } else { // 데이터가 1개라도 있으면 Recycler View를 보여줌
                    (binding?.myBoardRecyclerView?.adapter as MyBoardsRecyclerAdapter).updateData(data)
                    binding?.noResultView.visibility = View.GONE
                    binding?.myBoardRecyclerView.visibility = View.VISIBLE
                }
            }
        })

        // 데이터를 삭제해서 0개가 되면 No Result View 보여주기
        vm?.myBoardItems.observe(this, Observer { list ->
            if (list.size == 0) {
                binding?.noResultView.visibility = View.VISIBLE
                binding?.myBoardRecyclerView.visibility = View.GONE
            }
        })

        binding?.backBtn.setOnClickListener { finish() }
    }

    // 내가 쓴 글들 중 하나 클릭했을 때
    fun clickProduct(position: Int) {
        vm.selectedItemPosition = position
        val intent = Intent(this, BoardActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra("product_item", vm.myBoardItems.value!![position])
            .putExtra("from", "MyBoardsActivity")
        startActivityForResult(intent, REQUEST_CODE.DELETE_BOARD_CODE_FROM_MY_BOARD)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != RESULT_OK) return
        when (requestCode) {
            // 게시글을 삭제하면 view model의 데이터 리스트에서 삭제한 게시물 데이터를 제거하고
            // recycler view에 해당 게시물이 없어졌다고 알림.
            REQUEST_CODE.DELETE_BOARD_CODE_FROM_MY_BOARD -> {
                // 하트 상태를 갱신해야 하는 경우
                // 바뀐 좋아요 상태
                val heartState = data?.getBooleanExtra("heart", false)!!
                if (data?.getBooleanExtra("refresh", false)) { // 새로고침을 해야하는 경우라면
                    if(heartState) { // 바뀐 좋아요 상태가 true인 경우 추가
                        vm.myBoardItems.value!![vm.selectedItemPosition].favorites[Firebase.auth.uid!!] = true
                    } else { // 바뀐 좋아요 상태가 false인 경우 제거
                        vm.myBoardItems.value!![vm.selectedItemPosition].favorites.remove(Firebase.auth.uid!!)
                    }
                }else {
                    vm.myBoardItems.value!!.removeAt(vm.selectedItemPosition)
                    vm.myBoardItems.postValue(vm.myBoardItems.value)
                }
                (binding?.myBoardRecyclerView?.adapter as MyBoardsRecyclerAdapter).updateData(vm.myBoardItems.value!!)
            }
        }
    }
}