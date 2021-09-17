package com.app.buna.sharingmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.app.buna.sharingmarket.databinding.ImageSlideItemBinding
import com.app.buna.sharingmarket.model.SliderItem
import com.smarteist.autoimageslider.SliderViewAdapter


class ImageSliderAdapter(private val sliderItems: ArrayList<SliderItem>) :
    SliderViewAdapter<ImageSliderAdapter.SliderAdapterViewHolder>() {

    inner class SliderAdapterViewHolder(private val binding: ImageSlideItemBinding) :
        SliderViewAdapter.ViewHolder(binding.root) {

        fun bind(model: SliderItem) {
            binding.model = model
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup?): ImageSliderAdapter.SliderAdapterViewHolder {
        return SliderAdapterViewHolder(
            ImageSlideItemBinding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        viewHolder: ImageSliderAdapter.SliderAdapterViewHolder?,
        position: Int
    ) {
        viewHolder?.bind(sliderItems.get(position))
    }

    override fun getCount(): Int {
        return sliderItems.size
    }


}