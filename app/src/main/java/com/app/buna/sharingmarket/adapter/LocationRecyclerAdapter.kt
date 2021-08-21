package com.app.buna.sharingmarket.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.databinding.LocationItemBinding
import com.app.buna.sharingmarket.model.items.LocationItem
import com.app.buna.sharingmarket.viewmodel.InitialViewModel

class LocationRecyclerAdapter(val locationList : MutableLiveData<List<LocationItem>>, val viewModel: InitialViewModel, val context: Context)
    : RecyclerView.Adapter<LocationRecyclerAdapter.MyViewHolder>(){


    inner class MyViewHolder(val binding: LocationItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocationItem){
            binding.locationModel = item
            binding.itemLayout.setOnClickListener {
                viewModel.startNextFragmentWithSaving(context, item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(locationList?.value!![position])
    }

    override fun getItemCount(): Int {
        Log.d("LocationRecyclerAdapter", locationList?.value!!.size.toString())
        return locationList?.value!!.size
    }

    fun updateData() {
        notifyDataSetChanged()
    }


}