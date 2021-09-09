package com.app.buna.sharingmarket.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.MenuId
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE.Companion.UPDATE_BOARD_CODE
import com.app.buna.sharingmarket.adapter.ImageSliderAdapter
import com.app.buna.sharingmarket.databinding.ActivityBoardBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.utils.FancyToastUtil
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

        vm?.item =
            intent.getParcelableExtra<ProductItem>("product_item") // MainHomeFragment에서 전달받은 데이터
        vm.originHeartState = vm?.item.favorites.containsKey(vm.getUid())
        vm.nowHeartState = vm.originHeartState

        initBinding()
        initView()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board)
        binding?.viewModel = vm
        binding?.lifecycleOwner = this
    }

    private fun initView() {

        // 게시판 작성자 프로필 설정
        vm.getProfile(vm.item.uid) { profileUrl ->
            if (profileUrl != null) {
                vm.profileUrl = profileUrl
                Glide.with(this).load(Uri.parse(profileUrl)).error(R.drawable.default_profile).circleCrop()
                    .into(binding?.profileImageView!!)
            }
        }

        // 이미지 슬라이더 adapter 초기화
        if (vm?.item.imgPath.size > 1) { // 개수가 2개 이상이면 Image Slider 사용
            binding?.imageView?.visibility = View.GONE // Image View는 사라지게
            binding?.imageHolderView?.visibility = View.GONE // Image View를 감싸고 있는 Holder도 사라지게
            binding?.imageSlider?.apply {
                visibility = View.VISIBLE
                setSliderAdapter(ImageSliderAdapter(vm?.getSlideItem()))
                setIndicatorAnimation(IndicatorAnimationType.DROP)
            }
        } else { // 개수가 1개보다 적으면 Image View 사용
            // xml에서와 java에서의 value를 계산하는 방식이 다르므로 아래 로직 수행
            if (vm?.item.imgPath.size == 1) { // 이미지가 1개인 경우
                // ImageView의 크기 조정을 위한 디멘션
                val height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    300F,
                    resources.displayMetrics
                ).toInt()

                binding?.imageSlider?.visibility = View.GONE // Image Slider는 사라지게
                binding?.imageView?.let { imageView ->
                    imageView.layoutParams.height = height
                    imageView.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
                    imageView.requestLayout()

                    Glide.with(this).load(vm?.item.imgPath.values.first()).error(R.drawable.default_item).centerCrop()
                        .into(imageView)
                }
            }
        }

        // ''1:1 채팅하기 버튼'' or ''거래 완료''
        // 거래 완료
        if (vm?.getUid() == vm?.item.uid) { // 본인인 경우에 ''거래 완료''로 표시
            if (vm?.item.isComplete) { // 거래가 완료된 게시물인 경우
                binding?.chatBtn?.apply {
                    /*val btnDrawable = DrawableCompat.wrap(background)
                    DrawableCompat.setTint(btnDrawable, ContextCompat.getColor(this@BoardActivity, R.color.gray_50))
                    background = btnDrawable*/
                    backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this@BoardActivity,
                            R.color.gray_50
                        )
                    )
                    text = getString(R.string.share_not_done)
                    // 거래 완료 상태를 해제할지 물어봄
                    setOnClickListener {
                        AlertDialog.Builder(this@BoardActivity)
                            .setMessage(getString(R.string.share_not_done_message))
                            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                                FancyToastUtil(this@BoardActivity).showSuccess(getString(R.string.share_not_done_message))
                                vm?.shareDone(false) { // 나눔 완료로 상태 표시
                                    setResult(RESULT_OK)
                                    finish()
                                }
                            }.setNegativeButton(getString(R.string.cancel)) { dialog, id ->
                                dialog.dismiss()
                            }.create().show()
                    }
                }
            } else { // 거래가 완료된 게시물이 아니면 완료로 변경할건지 물어봄
                binding?.chatBtn?.apply {
                    // 게시글을 나눔 완료 상태로 변경할지 물어봄
                    text = getString(R.string.share_done)
                    setOnClickListener {
                        AlertDialog.Builder(this@BoardActivity)
                            .setTitle(getString(R.string.share_done))
                            .setMessage(getString(R.string.share_done_message))
                            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                                FancyToastUtil(this@BoardActivity).showSuccess(getString(R.string.share_done_message))
                                vm?.shareDone(true) { // 나눔 완료로 상태 표시
                                    setResult(RESULT_OK)
                                    finish()
                                }
                            }.setNegativeButton(getString(R.string.cancel)) { dialog, id ->
                                dialog.dismiss()
                            }.create().show()
                    }
                }
            }
        } else { // 1:1 채팅하기 = 본인이 아닌 경우에는 채팅 버튼으로 전환
            binding?.chatBtn?.text = getString(R.string.one_to_one_chat)
            if (vm?.item.isComplete) { // 거래 완료된 게시물이면 클릭 못하게 변경
                binding?.chatBtn?.isEnabled = false
                binding?.chatBtn?.isClickable = false
            } else { // 거래 완료된 게시물이 아니면 채팅 가능
                binding?.chatBtn?.isEnabled = true
                binding?.chatBtn?.isClickable = true
                binding?.chatBtn?.setOnClickListener {
                    // 1:1 채팅을 위해 게시글 작성자 uid를 넘기면서 화면 전환
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.let {
                        it.putExtra("userName", vm.item.owner) // 채팅할 상대방 닉네임 전달
                        it.putExtra("profileImageUrl", vm.profileUrl) // 채팅 상대방 프로필 Url 전달
                        it.putExtra("destUid", vm.item.uid) // 채팅 상대방 uid 전달
                    }

                    startActivity(intent)
                }
            }
        }

        // 툴바 사용
        setSupportActionBar(binding?.toolBar)

        // 뒤로가기 버튼
        binding?.backBtn?.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_board_tool_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> vm?.sendKakaoLink()
            R.id.action_heart -> {
                vm?.clickHeart { newState ->
                    // 새로운 상태가 좋아요 누른 상태이면
                    vm.nowHeartState  = newState
                    if (newState) {
                        item.icon = ContextCompat.getDrawable(this, R.drawable.like_heart)
                        vm.item.favorites[vm.getUid()!!] = true
                        return@clickHeart
                    } else { // 새로운 상태가 좋아요를 해제한 상태이면
                        item.icon = (ContextCompat.getDrawable(this, R.drawable.not_like_heart))
                        vm.item.favorites.remove(vm.getUid())
                        return@clickHeart
                    }
                }
            }
            MenuId.DELETE -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.delete_board)
                    .setMessage(R.string.delete_board_question)
                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        // 해당 게시글 삭제
                        vm?.removeBoard { isSuccessful ->
                            if (isSuccessful) { // 성공적으로 삭제했다면 
                                dialog.dismiss() // Dialog 닫고
                                // 내 게시글 보기 액티비티에서 실행한 경우
                                setResult(RESULT_OK)
                                finish() // 해당 게시글에서 나가기
                            }
                        }
                    }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()

            }
            MenuId.UPDATE -> {
                val updateIntent = Intent(this, UpdateActivity::class.java)
                updateIntent.putExtra("product_item", vm?.item)
                startActivityForResult(updateIntent, UPDATE_BOARD_CODE)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.removeItem(MenuId.DELETE)
        menu?.removeItem(MenuId.UPDATE)
        val uid = vm?.getUid()

        // 좋아요 누른 여부에 따라 Toolbar 아이콘 변경
        if (vm?.item.favorites.containsKey(uid)) { // 좋아요 목록에 본인인 포함되어 있으면 하트 활성화
            var heartIcon = ContextCompat.getDrawable(this, R.drawable.like_heart)
            menu?.findItem(R.id.action_heart)?.icon = heartIcon
        } else { // 좋아요 목록에 본인인 포함되어 있지 않으면 하트 비활성화
            var heartIcon = ContextCompat.getDrawable(this, R.drawable.not_like_heart)
            menu?.findItem(R.id.action_heart)?.icon = heartIcon
        }

        // 본인 게시글 여부에 따라 삭제 버튼 동적으로 추가 및 아이콘 배치 변경
        if (vm?.getUid() == vm?.item.uid) { // 본인 게시글이면
            // add() : (groupId, itemId, order, titleRes)
            menu?.add(0, MenuId.UPDATE, 0, getString(R.string.update))?.setIcon(R.drawable.app_icon)
                ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
            menu?.add(0, MenuId.DELETE, 1, getString(R.string.delete))?.setIcon(R.drawable.app_icon)
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

    override fun onBackPressed() {
        if(vm.isHeartStateChanged()) {
            setResult(RESULT_OK)
        }
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != RESULT_OK) {
            return
        }

        if (requestCode == UPDATE_BOARD_CODE) { // 게시글을 업데이트 했다면 뷰 갱신을 위한 resultCode
            setResult(RESULT_OK)
            finish()
        }
    }
}