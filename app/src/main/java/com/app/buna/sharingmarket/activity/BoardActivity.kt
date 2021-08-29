package com.app.buna.sharingmarket.activity

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.MENU_ID
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.REQUEST_CODE.Companion.UPDATE_BOARD_CODE
import com.app.buna.sharingmarket.adapter.ImageSliderAdapter
import com.app.buna.sharingmarket.databinding.ActivityBoardBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.viewmodel.BoardViewModel
import com.bumptech.glide.Glide
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

        vm?.item = intent.getParcelableExtra<ProductItem>("product_item") // MainHomeFragment에서 전달받은 데이터
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

        // 툴바 사용
        setSupportActionBar(binding?.toolBar)

        // 뒤로가기 버튼
        binding?.backBtn?.setOnClickListener {
            finish()
        }
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
            R.id.action_heart -> {
                vm?.clickHeart { newState ->
                    // 새로운 상태가 좋아요 누른 상태이면
                    if (newState) {
                        item.icon = ContextCompat.getDrawable(this, R.drawable.like_heart)
                        return@clickHeart
                    } else { // 새로운 상태가 좋아요를 해제한 상태이면
                        item.icon = (ContextCompat.getDrawable(this, R.drawable.not_like_heart))
                        return@clickHeart
                    }
                }
            }
            MENU_ID.DELETE -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.delete_board)
                    .setMessage(R.string.delete_board_question)
                    .setPositiveButton(getString(R.string.ok)) { dialog, id ->
                        // 해당 게시글 삭제
                        vm?.removeBoard { isSuccessful ->
                            if (isSuccessful) { // 성공적으로 삭제했다면 
                                dialog.dismiss() // Dialog 닫고
                                //setResult(REQUEST_CODE.DELETE_BOARD_CODE) // NOT WORKING
                                finish() // 해당 게시글에서 나가기
                            }
                        }
                    }.setNegativeButton(getString(R.string.cancel)) { dialog, id ->
                        dialog.dismiss()
                    }.create().show()

            }
            MENU_ID.UPDATE -> {
                val updateIntent = Intent(this, UpdateActivity::class.java)
                updateIntent.putExtra("product_item", vm?.item)
                startActivityForResult(updateIntent, UPDATE_BOARD_CODE)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.removeItem(MENU_ID.DELETE)
        menu?.removeItem(MENU_ID.UPDATE)
        val uid = vm?.getUid()

        // 좋아요 누른 여부에 따라 Toolbar 아이콘 변경
        if (vm?.item.favorites.containsKey(uid)) { // 좋아요 목록에 본인인 포함되어 있으면 하트 활성화
            var heart_icon = ContextCompat.getDrawable(this, R.drawable.like_heart)
            menu?.findItem(R.id.action_heart)?.setIcon(heart_icon)
        } else { // 좋아요 목록에 본인인 포함되어 있지 않으면 하트 비활성화
            var heart_icon = ContextCompat.getDrawable(this, R.drawable.not_like_heart)
            menu?.findItem(R.id.action_heart)?.setIcon(heart_icon)
        }

        // 본인 게시글 여부에 따라 삭제 버튼 동적으로 추가 및 아이콘 배치 변경
        if (vm?.getUid() == vm?.item.uid) { // 본인 게시글이면
            // add() : (groupId, itemId, order, titleRes)
            menu?.add(0, MENU_ID.UPDATE, 0, getString(R.string.update))?.setIcon(R.drawable.app_icon)
                ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
            menu?.add(0, MENU_ID.DELETE, 1, getString(R.string.delete))?.setIcon(R.drawable.app_icon)
                ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
            //menu?.findItem(R.id.action_share)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        } else { // 본인 게시글이 아니면
            // 기존 메뉴 방식으로 짆애
        }

        return true
    }

    override fun onOptionsMenuClosed(menu: Menu?) {
        super.onOptionsMenuClosed(menu)
    }
}