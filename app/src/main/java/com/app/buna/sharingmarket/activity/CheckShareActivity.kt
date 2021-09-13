package com.app.buna.sharingmarket.activity

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
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

        // 현재 모든 게시글의 개수를 가져와서 text를 변경해준다.
        vm.getBoardTotalCount { boardTotalCount ->
            binding?.shareCountText?.apply {
                val span = SpannableStringBuilder(getString(R.string.now_share_count, boardTotalCount))
                span.setSpan(StyleSpan(Typeface.BOLD), 10, 10+boardTotalCount.toString().length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                span.setSpan(RelativeSizeSpan(1.5f), 10, 10+boardTotalCount.toString().length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)

                text = span
            }
            // 게시물 개수 부분 굵게

        }

    }
}