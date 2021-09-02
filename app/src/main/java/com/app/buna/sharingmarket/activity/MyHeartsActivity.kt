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
import com.app.buna.sharingmarket.adapter.MyHeartsRecyclerAdapter
import com.app.buna.sharingmarket.callbacks.IFirebaseGetStoreDataCallback
import com.app.buna.sharingmarket.databinding.ActivityMyHeartsBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.viewmodel.MyBoardViewModel

class MyHeartsActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyHeartsBinding
    val vm: MyBoardViewModel by lazy {
        ViewModelProvider(
            this,
            MyBoardViewModel.Factory(application, this)
        ).get(MyBoardViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_hearts)
        binding.lifecycleOwner = this
        binding.viewModel = vm

        initView()
    }

    fun initView() {
        binding?.myHeartsRecyclerView.adapter = MyHeartsRecyclerAdapter(vm, this, this)
        binding?.myHeartsRecyclerView?.layoutManager = LinearLayoutManager(this)

        vm?.getMyHearts(object : IFirebaseGetStoreDataCallback {
            override fun complete(data: ArrayList<ProductItem>) {
                vm.myBoardItems.postValue(data) // viewmodel의 리스트 데이터들도 갱신

                if (data.size == 0) { // 데이터가 0개면 No Result View를 보여줌
                    binding?.myHeartsRecyclerView.visibility = View.GONE
                    binding?.noResultView.visibility = View.VISIBLE
                } else { // 데이터가 1개라도 있으면 Recycler View를 보여줌
                    (binding?.myHeartsRecyclerView?.adapter as MyHeartsRecyclerAdapter).updateData(data)
                    binding?.noResultView.visibility = View.GONE
                    binding?.myHeartsRecyclerView.visibility = View.VISIBLE
                }
            }
        })

        // 데이터를 삭제해서 0개가 되면 No Result View 보여주기
        vm?.myBoardItems.observe(this, Observer { list ->
            if (list.size == 0) {
                binding?.noResultView.visibility = View.VISIBLE
                binding?.myHeartsRecyclerView.visibility = View.GONE
            }
        })
    }

    // 내가 쓴 글들 중 하나 클릭했을 때
    fun clickProduct(position: Int) {
        vm.selectedItemPosition = position
        val intent = Intent(this, BoardActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra("product_item", vm.myBoardItems.value!![position])
        startActivityForResult(intent, REQUEST_CODE.DELETE_BOARD_CODE_FROM_MY_HEART)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != RESULT_OK) return

        when(requestCode) {
            REQUEST_CODE.DELETE_BOARD_CODE_FROM_MY_HEART -> {
                vm.myBoardItems.value!!.removeAt(vm.selectedItemPosition)
                vm.myBoardItems.postValue(vm.myBoardItems.value)
                (binding?.myHeartsRecyclerView?.adapter as MyHeartsRecyclerAdapter).updateData(vm.myBoardItems.value!!)
            }
        }
    }
}