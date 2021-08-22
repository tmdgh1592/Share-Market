package com.app.buna.sharingmarket.activity

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.callbacks.FirebaseRepositoryCallback
import com.app.buna.sharingmarket.databinding.ActivityWriteBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.app.buna.sharingmarket.viewmodel.WriteViewModel
import com.github.hamzaahmedkhan.spinnerdialog.OnSpinnerOKPressedListener
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerDialogFragment.Companion.newInstance
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerModel


private var binding: ActivityWriteBinding? = null
private var vm: WriteViewModel? = null


class WriteActivity : AppCompatActivity(), FirebaseRepositoryCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initView()
    }

    private fun initBinding() {
        vm = ViewModelProvider(
            this,
            WriteViewModel.Factory(application)
        ).get(WriteViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write)
        binding?.lifecycleOwner = this
        binding?.viewModel = vm
    }

    fun initView() {
        // 카테고리 Spinner 아이템 선택 리스너
        val categoryList = ArrayList<SpinnerModel>()
        resources.getStringArray(R.array.category).forEach {
            categoryList.add(SpinnerModel(it))
        }

        // Init 스피너 Fragment
        val spinnerDialogFragment = newInstance(
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



        // 카테고리 스피너 다이얼로그
        binding?.categorySpinner?.setOnClickListener {
            // 카테고리 선택창 클릭시 키보드 닫기
            hideKeyBoard()
            // 카테고리 스피너 다이얼로그 보이기
            spinnerDialogFragment.show(
                supportFragmentManager,
                "SpinnerDialogFragment"
            )
        }

        // 완료 버튼 -> 게시글 업로드
        binding?.doneBtn?.setOnClickListener {
            // ex) ProductItem("1", "","Test1","","행신동","1분 전", ArrayList(),"", "", 10, false)
            hideKeyBoard()
            val item = ProductItem(
                owner = vm?.getUserName()!!,  // 상품 주인
                category = vm?.category!!, // 카테고리
                location = vm?.getUserInfo("jibun")!!, // 지역
                time = System.currentTimeMillis().toString(), // 게시글 업로드 시간
                uri = ArrayList(), // 이미지들 uri
                title = binding?.titleEditText?.text.toString(), // 게시글 제목
                content = binding?.contentEditText?.text.toString(), // 게시글 내용
                likeCount = 0, // 좋아요 개수
                isComplete = false, // 거래 완료된지 여부
                isGive = vm?.isGive!! // 주는건지 받는건지
            )
            vm?.uploadProduct(item, this)
        }

        // Radio Button (드릴게요!, 필요해요!)
        binding?.typeRadioGroup?.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, @IdRes id: Int) {
                hideKeyBoard()
                when(id) {
                    R.id.radio_give -> vm?.isGive = true
                    R.id.radio_need -> vm?.isGive = false
                }
            }
        })
    }

    /* Firebase 업로드에 성공한 경우 callback 지정 */
    override fun callbackForSuccessfulUploading() {
        FancyChocoBar(this).showSnackBar(getString(R.string.upload_success))
    }

    /* Firebase 업로드에 실패한 경우 callback 지정 */
    override fun callbackForFailureUploading() {
        FancyChocoBar(this).showAlertSnackBar(getString(R.string.upload_fail))
    }

    // 키보드 닫기
    fun hideKeyBoard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
            binding?.titleEditText?.windowToken,
            0
        )
    }
}