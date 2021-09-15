package com.app.buna.sharingmarket.activity

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.ActivityCheckShareBinding
import com.app.buna.sharingmarket.viewmodel.CheckShareViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class CheckShareActivity : AppCompatActivity() {
    var binding: ActivityCheckShareBinding? = null
    val vm: CheckShareViewModel by lazy {
        ViewModelProvider(
            this,
            CheckShareViewModel.Factory(application)
        ).get(CheckShareViewModel::class.java)
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
                // 게시물 개수 부분 굵게
                val span =
                    SpannableStringBuilder(getString(R.string.now_share_count, boardTotalCount))
                span.setSpan(
                    StyleSpan(Typeface.BOLD),
                    10,
                    10 + boardTotalCount.toString().length,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
                span.setSpan(
                    RelativeSizeSpan(1.5f),
                    10,
                    10 + boardTotalCount.toString().length,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )

                text = span
            }

            // 숫자에 1000단위마다 ,를 나타내기위한 포맷
            val decimalFormat = DecimalFormat("###,###.###")
            // 나무 사용량 : 0.2배
            // 이산화탄소 배출량 : 폐기물 1kg당 120g -> 나눔 제품 대략 3.5kg로 잡고 계산
            // 플라스틱 배출량 : 플라스틱 1인당 년간 145,000g 사용 -> 365일로 나누어 8로 나눔.
            binding?.realtimeEnvTitle1?.text = getString(R.string.realtime_env_title_1, decimalFormat.format(0.2f * boardTotalCount).toString())
            binding?.realtimeEnvTitle2?.text = getString(R.string.realtime_env_title_2, decimalFormat.format(24.5f * boardTotalCount).toString())
            binding?.realtimeEnvTitle3?.text = getString(R.string.realtime_env_title_3, decimalFormat.format(3.5f * boardTotalCount).toString())
            binding?.realtimeEnvTitle4?.text = getString(R.string.realtime_env_title_4, decimalFormat.format(0.185f * boardTotalCount).toString())
            binding?.realtimeEnvTitle5?.text = getString(R.string.realtime_env_title_5, decimalFormat.format(0.01f * boardTotalCount).toString())
        }

        // 애니메이션 보여주기
        CoroutineScope(Dispatchers.Main).launch {
            binding?.mainLottieImage?.startAnimation(
                AnimationUtils.loadAnimation(
                    this@CheckShareActivity,
                    R.anim.anim_lottie_fade_in
                )
            )
        }


    }
}