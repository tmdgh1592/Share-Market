package com.app.buna.sharingmarket.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import com.app.buna.sharingmarket.databinding.CategoryItemBinding
import com.app.buna.sharingmarket.model.items.CategoryItem

class CategoryGridAdapter(val context: Context, val categoryList: ArrayList<CategoryItem>) :
    BaseAdapter() {

    override fun getCount(): Int {
        if (categoryList != null) {
            return categoryList.size
        }
        return 0
    }

    override fun getItem(position: Int): Any {
        return categoryList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        binding?.categoryItemLayout
        binding.categoryModel = categoryList.get(position)

        return binding.root

    }
}