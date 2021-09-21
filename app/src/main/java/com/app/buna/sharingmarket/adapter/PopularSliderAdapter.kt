package com.app.buna.sharingmarket.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.databinding.ItemPopularBoardBinding
import com.app.buna.sharingmarket.model.main.BoardItem
import com.bumptech.glide.Glide
import com.github.islamkhsh.CardSliderAdapter

// 관심 있는 게시글 슬라이더 어댑터
class PopularSliderAdapter : CardSliderAdapter<PopularSliderAdapter.SliderViewHolder>() {

    var sliderItems = ArrayList<BoardItem>()

    inner class SliderViewHolder(val binding: ItemPopularBoardBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = sliderItems[position]
            binding.model = item
            if (item.imgPath.size > 0) {
                Glide.with(binding.productImageView.context).load(Uri.parse(item.imgPath.values.first()))
                    .into(binding.productImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(ItemPopularBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun bindVH(holder: SliderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }

    fun update(newSliderItems: ArrayList<BoardItem>) {
        sliderItems = newSliderItems
        notifyDataSetChanged()
    }

}