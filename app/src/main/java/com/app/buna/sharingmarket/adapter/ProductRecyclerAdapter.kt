package com.app.buna.sharingmarket.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.ProductItemBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.viewmodel.MainViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class ProductRecyclerAdapter(var viewModel: MainViewModel) : RecyclerView.Adapter<ProductRecyclerAdapter.ProductViewHolder>(){

    val productItemList = MutableLiveData<List<ProductItem>>(ArrayList())

    class ProductViewHolder(val binding: ProductItemBinding): RecyclerView.ViewHolder(binding.root){
        val productImageView = binding?.productImageView

        fun bind(item: ProductItem) {
            binding.model = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = productItemList?.value!!.get(position)

        // 이미지가 한개라도 있는 경우에만 제품 프로필 설정
        if (item.imgPath.size > 0) {
            Glide.with(holder.itemView).load(item.imgPath.values.first())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.no_image)
                .into(holder.productImageView)
        }else{ // 이미지가 한개도 없는 경우엔 기본 이미지로 대체
            Glide.with(holder.itemView).load(R.drawable.no_image)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.no_image)
                .into(holder.productImageView)
        }
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return productItemList?.value!!.size
    }

    fun updateData(newList: List<ProductItem>) {
        this.productItemList.value = newList
        notifyDataSetChanged()
    }

}