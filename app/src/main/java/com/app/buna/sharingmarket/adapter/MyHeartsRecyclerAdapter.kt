package com.app.buna.sharingmarket.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.MyHeartsActivity
import com.app.buna.sharingmarket.databinding.ItemMyBoardBinding
import com.app.buna.sharingmarket.model.BoardItem
import com.app.buna.sharingmarket.viewmodel.MyBoardViewModel

class MyHeartsRecyclerAdapter(val viewModel: MyBoardViewModel, val context : Context, val activity: Activity) :
    RecyclerView.Adapter<MyHeartsRecyclerAdapter.MyViewHolder>() {

    var myBoardList: ArrayList<BoardItem> = ArrayList()

    class MyViewHolder(val binding: ItemMyBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        val typeTextView = binding?.productType
        val frameView = binding?.frameView
        val completeView = binding?.completeView

        fun bind(item: BoardItem) {
            binding.model = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemMyBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = myBoardList[position]

        // 제품 타입(give, exchange) 입력
        if (!item.isExchange) { // 교환이 아닌 경우
            if (item.isGive) { // 나눔
                holder.typeTextView.setText("나눔")
                holder.typeTextView.background.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.give_color
                    )
                )
            } else { // 필요
                holder.typeTextView.setText("요청")
                holder.typeTextView.background.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.need_color
                    )
                )
            }
        } else if (item.isExchange) { // 물물교환인 경우
            holder.typeTextView.setText("물물교환")
            holder.typeTextView.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.exchange_color
                )
            ) // 노란색
        }

        // 제품 타입 추가 입력
        if (item.isComplete) { // 거래 완료 상태인 경우
            holder.typeTextView.setText("나눔완료")
            holder.typeTextView.background.setTint(
                ContextCompat.getColor(
                    context,
                    R.color.complete_color
                )
            ) // 노란색
        }

        // 게시글 클릭
        holder.frameView.setOnClickListener {
            // 클릭한 포지션 전달
            (activity as MyHeartsActivity).clickProduct(position)
        }

        if (item.isComplete) { // 나눔 완료 상태인 경우
            holder.completeView.visibility = View.VISIBLE
        } else {
            holder.completeView.visibility = View.GONE
        }

        holder.bind(item)
    }

    override fun getItemCount(): Int {
        if(myBoardList != null) {
            return myBoardList.size
        }
        return 0
    }

    fun updateData(data: ArrayList<BoardItem>) {
        myBoardList = data
        notifyDataSetChanged()
    }

}