package com.app.buna.sharingmarket.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.databinding.ItemFaqBinding
import com.app.buna.sharingmarket.model.faq.FaqItem
import com.app.buna.sharingmarket.utils.ToggleAnimation

class FAQRecyclerAdapter(var faqList: ArrayList<FaqItem>) : RecyclerView.Adapter<FAQRecyclerAdapter.FAQViewHolder>(){

    inner class FAQViewHolder(val binding: ItemFaqBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.faqModel = faqList[position]
            binding.faqLayout.setOnClickListener {
                val show = toggleLayout(!faqList[position].isExpanded, binding?.imgMore, binding?.layoutExpand)
                faqList[position].isExpanded = show
            }
        }

        private fun toggleLayout(isExpanded: Boolean, toggleView: View, layoutExpand: LinearLayout): Boolean {
            ToggleAnimation.toggleArrow(toggleView, isExpanded)
            if (isExpanded) {
                ToggleAnimation.expand(layoutExpand)
            } else {
                ToggleAnimation.collapse(layoutExpand)
            }
            return isExpanded
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FAQViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return faqList.size
    }

}