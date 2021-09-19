package com.app.buna.sharingmarket.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.activity.CheckShareActivity
import com.app.buna.sharingmarket.databinding.BoardItemBinding
import com.app.buna.sharingmarket.databinding.LayoutCheckShareBinding
import com.app.buna.sharingmarket.model.BoardItem
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class BoardRecyclerAdapter(var viewModel: MainViewModel, val context: Context) :
    RecyclerView.Adapter<BoardRecyclerAdapter.BaseViewHolder>() {

    var productItemList = ArrayList<BoardItem>()
    val CHECK_SHARE_VIEW = 0
    val BOARD_VIEW = 1

    abstract class BaseViewHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(item: BoardItem)
    }

    // 게시판 View Holder
    class ProductViewHolder(val binding: BoardItemBinding) :
        BaseViewHolder(binding) {
        val productImageView = binding?.productImageView
        val typeTextView = binding?.productType
        val frameView = binding?.frameView
        val completeView = binding?.completeView

        override fun bind(item: BoardItem) {
            binding.model = item
        }
    }


    // 나눔 현황 확인하기 View Holder
    class CheckShareViewHolder(val binding: LayoutCheckShareBinding) :
        BaseViewHolder(binding) {

        override fun bind(item: BoardItem) {
            binding.model = item
            binding.checkShareLayout.setOnClickListener {
                it.context.startActivity(Intent(it.context, CheckShareActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        when (viewType) {
            BOARD_VIEW -> {
                val binding =
                    BoardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ProductViewHolder(binding)
            }
            CHECK_SHARE_VIEW -> {
                val binding = LayoutCheckShareBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CheckShareViewHolder(binding)
            }
            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            // 제품 게시글 뷰 홀더이면
            is ProductViewHolder -> {

                val item = productItemList[position]

                // 이미지가 한개라도 있는 경우에만 제품 프로필 설정
                if (item.imgPath.size > 0) {
                    Glide.with(holder.itemView).load(item.imgPath.values.first())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.default_item)
                        .into(holder.productImageView)
                } else { // 이미지가 한개도 없는 경우엔 기본 이미지로 대체
                    Glide.with(holder.itemView).load(R.drawable.default_item)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.default_item)
                        .into(holder.productImageView)
                }

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
                    holder.typeTextView.text = "물물교환"
                    holder.typeTextView.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.exchange_color
                        )
                    ) // 노란색
                }

                // 제품 타입 추가 입력
                if (item.isComplete) { // 거래 완료 상태인 경우
                    holder.typeTextView.text = "거래완료"

                    holder.typeTextView.background.setTint(
                        ContextCompat.getColor(
                            context,
                            R.color.complete_color
                        )
                    ) // 노란색
                }


                holder.frameView.setOnClickListener {
                    viewModel.clickProduct(position)
                }

                if (item.isComplete) { // 나눔 완료 상태인 경우
                    holder.completeView.visibility = View.VISIBLE
                } else {
                    holder.completeView.visibility = View.GONE
                }

                holder.bind(item)
            }
            // 나눔 현황 확인하기 뷰 홀더이면
            is CheckShareViewHolder -> {
                holder.bind(productItemList[position])
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return productItemList[position].hashCode().toLong()
    }

    override fun getItemCount(): Int {
        if (productItemList != null) return productItemList.size
        return 0
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return CHECK_SHARE_VIEW
        }
        return BOARD_VIEW
    }

    fun updateData(newList: ArrayList<BoardItem>) {
        this.productItemList = newList
        notifyDataSetChanged()
    }

}