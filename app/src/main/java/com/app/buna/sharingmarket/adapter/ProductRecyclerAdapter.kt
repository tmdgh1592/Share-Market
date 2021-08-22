package com.app.buna.sharingmarket.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.databinding.LocationItemBinding
import com.app.buna.sharingmarket.databinding.ProductItemBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.viewmodel.MainViewModel

class ProductRecyclerAdapter(var productItemList: MutableLiveData<List<ProductItem>>, var viewModel: MainViewModel) : RecyclerView.Adapter<ProductRecyclerAdapter.ProductViewHolder>(){

    class ProductViewHolder(val binding: ProductItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(item: ProductItem) {
            binding.model = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productItemList?.value!!.get(position))
    }

    override fun getItemCount(): Int {
        Log.d("ProductRecyclerAdapter", productItemList?.value!!.size.toString())
        return productItemList?.value!!.size
    }

    fun updateData() {
        notifyDataSetChanged()
    }

}