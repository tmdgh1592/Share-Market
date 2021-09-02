package com.app.buna.sharingmarket.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.ProductItemBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ProductRecyclerAdapter(var viewModel: MainViewModel, val context: Context) :
    RecyclerView.Adapter<ProductRecyclerAdapter.ProductViewHolder>() {

    var productItemList = ArrayList<ProductItem>()

    class ProductViewHolder(val binding: ProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val productImageView = binding?.productImageView
        val typeTextView = binding?.productType
        val frameView = binding?.frameView
        val completeView = binding?.completeView

        fun bind(item: ProductItem) {
            binding.model = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
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
                holder.typeTextView.setText("필요")
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

    override fun getItemCount(): Int {
        if (productItemList != null) return productItemList.size
        return 0
    }

    fun updateData(newList: ArrayList<ProductItem>) {
        this.productItemList = newList
        notifyDataSetChanged()
    }

}