package com.app.buna.sharingmarket.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.databinding.LocationItemBinding
import com.app.buna.sharingmarket.model.LocationItem
import com.app.buna.sharingmarket.viewmodel.InitialViewModel

class LocationRecyclerAdapter(
    val locationList: MutableLiveData<List<LocationItem>>,
    val viewModel: InitialViewModel,
    val context: Context,
    val isDialog: Boolean // 어디서 실행시켰는지 구분하기 위한 변수
) : RecyclerView.Adapter<LocationRecyclerAdapter.MyViewHolder>() {


    inner class MyViewHolder(val binding: LocationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LocationItem) {
            binding.locationModel = item
            binding.itemLayout.setOnClickListener {
                if (isDialog) { // LocationFragmentDialog에서 실행시켰다면
                    viewModel.saveLocationAndDismiss(context, item)
                } else { // InitialSecondFragment에서 실행시켰다면
                    viewModel.startNextFragmentWithSaving(context, item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LocationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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