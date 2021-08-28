package com.app.buna.sharingmarket.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.databinding.ActivityUpdateBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.repository.PreferenceUtil
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.app.buna.sharingmarket.utils.KeyboardUtil.Companion.hideKeyBoard
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.app.buna.sharingmarket.viewmodel.BoardViewModel
import com.app.buna.sharingmarket.viewmodel.WriteViewModel
import com.github.hamzaahmedkhan.spinnerdialog.OnSpinnerOKPressedListener
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerDialogFragment
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerModel
import com.opensooq.supernova.gligar.GligarPicker
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateBinding
    val boardVM: BoardViewModel by viewModel()
    private val vm: WriteViewModel by lazy {
        ViewModelProvider(this, WriteViewModel.Factory(get())).get(WriteViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        boardVM?.item = intent.getParcelableExtra<ProductItem>("product_item")
        initBinding()
        initView()

    }

    fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update)
        binding?.lifecycleOwner = this
        binding?.viewModel = boardVM
    }

    fun initView() {

        // 카테고리 Spinner 아이템 선택 리스너
        val categoryList = ArrayList<SpinnerModel>()
        resources.getStringArray(R.array.category).forEach {
            categoryList.add(SpinnerModel(it))
        }

        // Init 스피너 Fragment
        val spinnerDialogFragment = SpinnerDialogFragment.newInstance(
            getString(R.string.category), categoryList,
            object : OnSpinnerOKPressedListener {
                override fun onItemSelect(data: SpinnerModel, selectedPosition: Int) {
                    binding?.categoryText?.text = data.text
                    vm?.category = (data.text) // 선택한 카테고리를 뷰 모델에 저장
                }

            }, 0
        )
        spinnerDialogFragment.buttonText = getString(R.string.done)
        spinnerDialogFragment.themeColorResId = R.color.app_green

        /* 갤러리 포토 피커 */
        binding?.photoAddBtn?.setOnClickListener {
            GligarPicker()
                .requestCode(REQUEST_CODE.IMAGE_PICKER_REQUEST_CODE)
                .withActivity(this)
                .cameraDirect(false) // 열자마자 바로 카메라로 이동할지 여부
                .limit(5) // 이미지 선택 개수 제한
                .show()
        }

        // 카테고리 스피너 다이얼로그
        binding?.categorySpinner?.setOnClickListener {
            // 카테고리 선택창 클릭시 키보드 닫기
            hideKeyBoard(this@UpdateActivity, binding?.titleEditText!!.windowToken)
            // 카테고리 스피너 다이얼로그 보이기
            spinnerDialogFragment.show(
                supportFragmentManager,
                "SpinnerDialogFragment"
            )
        }

        // 완료 버튼 -> 게시글 수정
        binding?.doneBtn?.setOnClickListener {
            hideKeyBoard(this@UpdateActivity, binding?.titleEditText!!.windowToken)

            val location = PreferenceUtil.getString(this, "jibun", "null")
            val title = binding?.titleEditText?.text.toString()
            val content = binding?.contentEditText?.text.toString()

            if (title.isEmpty()) { // 제목이 비어 있는 경우
                FancyChocoBar(this).showOrangeSnackBar(getString(R.string.title_empty))
                return@setOnClickListener
            } else if (title.length < 2) { // 제목 길이가 2글자 미만인 경우
                FancyChocoBar(this).showOrangeSnackBar(getString(R.string.title_not_enough))
                return@setOnClickListener
            } else if (content.isEmpty()) { // 내용이 비어 있는 경우
                FancyChocoBar(this).showOrangeSnackBar(getString(R.string.content_empty))
                return@setOnClickListener
            } else if (content.length < 10) { // 내용이 충분하지 않은 경우
                FancyChocoBar(this).showOrangeSnackBar(getString(R.string.content_not_enough))
                return@setOnClickListener
            } else if (vm?.category == null) { // 카테고리가 선택되지 않은 경우
                FancyChocoBar(this).showOrangeSnackBar(getString(R.string.category_empty))
                return@setOnClickListener
            } else if (vm?.isGive == null) { // 라디오 버튼을 선택하지 않은 경우
                FancyChocoBar(this).showOrangeSnackBar(getString(R.string.option_empty))
                return@setOnClickListener
            } else if (!NetworkStatus.isConnectedInternet(this)) { // 인터넷 연결이 되어있지 않은 경우
                FancyChocoBar(this).showOrangeSnackBar(getString(R.string.internet_check))
                return@setOnClickListener
            } else if (location == null) { // 지역 데이터를 가져오지 못한 경우
                FancyChocoBar(this).showAlertSnackBar(getString(R.string.upload_fail))
                return@setOnClickListener
            }

            for (i in 1..vm?.imagePaths?.size!!) {
                vm?.fileNameForDelete?.add(i.toString())
            }

            val item = ProductItem(
                uid = vm?.getUid()!!,
                owner = vm?.getUserName()!!,  // 상품 주인
                category = vm?.category!!, // 카테고리
                location = location!!, // 지역
                time = System.currentTimeMillis(), // 게시글 업로드 시간
                title = title, // 게시글 제목
                content = content, // 게시글 내용
                likeCount = 0, // 좋아요 개수
                isComplete = false, // 거래 완료된지 여부
                isGive = vm?.isGive!!, // 주는건지 받는건지
                isExchange = false, // 무료나눔 Activity이기 때문에 교환은 false 처리
                fileNamesForDelete = vm?.fileNameForDelete!!
            )

            vm?.updateProduct(item) // 게시글 데이터 업데이트

        }

        // 카테고리 스피너 다이얼로그
        binding?.categorySpinner?.setOnClickListener {
            // 카테고리 선택창 클릭시 키보드 닫기
            hideKeyBoard(this@UpdateActivity, binding?.titleEditText!!.windowToken)
            // 카테고리 스피너 다이얼로그 보이기
            spinnerDialogFragment.show(
                supportFragmentManager,
                "SpinnerDialogFragment"
            )
        }
    }
}