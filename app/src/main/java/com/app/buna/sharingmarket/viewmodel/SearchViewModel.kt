package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(application) as T
        }
    }


    var keyword: String = ""

    
    // 자동완성 키워드 반환
    fun getList(context: Context): List<String> {
        return context.resources.getStringArray(R.array.auto_complete_keywords).toList()
    }
}