package com.app.buna.sharingmarket.activity

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.adapter.ImageSliderAdapter
import com.app.buna.sharingmarket.databinding.ActivityBoardBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.viewmodel.BoardViewModel
import com.bumptech.glide.Glide
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.template.model.*
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import org.koin.android.ext.android.get

class BoardActivity : AppCompatActivity() {

    private var binding: ActivityBoardBinding? = null
    private val vm: BoardViewModel by lazy {
        ViewModelProvider(this, BoardViewModel.Factory(get(), this))
            .get(BoardViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm?.item =
            intent.getParcelableExtra<ProductItem>("product_item") // MainHomeFragment에서 전달받은 데이터
        initBinding()
        initView()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board)
        binding?.viewModel = vm
        binding?.lifecycleOwner = this
    }

    private fun initView() {

        // 이미지 슬라이더 adapter 초기화
        if (vm?.item.imgPath.size > 1) { // 개수가 2개 이상이면 Image Slider 사용
            binding?.imageView?.visibility = View.GONE // Image View는 사라지게
            binding?.imageSlider?.apply {
                visibility = View.VISIBLE
                setSliderAdapter(ImageSliderAdapter(vm?.getSlideItem()))
                setIndicatorAnimation(IndicatorAnimationType.DROP)
            }
        } else { // 개수가 1개보다 적으면 Image View 사용

            // xml에서와 java에서의 value를 계산하는 방식이 다르므로 아래 로직 수행
            val height = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                300F,
                getResources().getDisplayMetrics()
            ).toInt()

            binding?.imageSlider?.visibility = View.GONE // Image Slider는 사라지게
            binding?.imageView?.let { imageView ->
                imageView.visibility = View.VISIBLE
                if (vm?.item.imgPath.size == 1) { // 이미지가 1개인 경우엔 ImageView를 사용
                    imageView.layoutParams.height = height
                    imageView.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                    imageView.requestLayout()

                    Glide.with(this).load(vm?.item.imgPath.values.first()).centerCrop().into(
                        imageView
                    )
                }
            }
        }

        setSupportActionBar(binding?.toolBar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_board_tool_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> vm?.sendKakaoLink()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}