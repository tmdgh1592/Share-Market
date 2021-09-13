package com.app.buna.sharingmarket.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.ActivityCheckShareBinding
import com.app.buna.sharingmarket.viewmodel.CheckShareViewModel

class CheckShareActivity : AppCompatActivity() {
    var binding: ActivityCheckShareBinding? = null
    val vm: CheckShareViewModel by lazy {
        ViewModelProvider(this, CheckShareViewModel.Factory(application)).get(CheckShareViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    // View 초기화
    fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_share)
        binding?.lifecycleOwner = this
        binding?.viewModel = vm
    }
}