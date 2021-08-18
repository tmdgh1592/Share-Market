package com.app.buna.sharemarket.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharemarket.databinding.LocationItemBinding
import com.app.buna.sharemarket.model.LocationItem

class LocationRecyclerAdapter(val locationList : MutableLiveData<ArrayList<LocationItem>>)
    : RecyclerView.Adapter<LocationRecyclerAdapter.MyViewHolder>(){

    class MyViewHolder(val binding: LocationItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocationItem){
            binding.locationModel = item
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

    fun updateData(data: ArrayList<LocationItem>) {
        notifyDataSetChanged()
    }


}