package com.app.buna.sharingmarket.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.Const
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.callbacks.IFirebaseRepositoryCallback
import com.app.buna.sharingmarket.databinding.ActivityWriteBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.repository.Local.PreferenceUtil
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.utils.KeyboardUtil.Companion.hideKeyBoard
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.app.buna.sharingmarket.viewmodel.WriteViewModel
import com.bumptech.glide.Glide
import com.github.hamzaahmedkhan.spinnerdialog.OnSpinnerOKPressedListener
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerDialogFragment.Companion.newInstance
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerModel
import com.opensooq.supernova.gligar.GligarPicker
import kotlinx.android.synthetic.main.picked_photo_view.view.*

private var binding: ActivityWriteBinding? = null
private var vm: WriteViewModel? = null

class WriteActivity : AppCompatActivity(), IFirebaseRepositoryCallback {
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
        resources.getStringArray(R.array.category_title).forEach {
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
        spinnerDialogFragment.themeColorResId = ContextCompat.getColor(this, R.color.app_green)

        /* 갤러리 포토 피커 */
        binding?.photoAddBtn?.setOnClickListener {
            GligarPicker()
                .requestCode(REQUEST_CODE.IMAGE_PICKER_REQUEST_CODE)
                .withActivity(this)
                .cameraDirect(false) // 열자마자 바로 카메라로 이동할지 여부
                .limit(Const.MAX_PHOTO_SIZE) // 이미지 선택 개수 제한
                .show()
        }

        // 카테고리 스피너 다이얼로그
        binding?.categorySpinner?.setOnClickListener {
            // 카테고리 선택창 클릭시 키보드 닫기
            hideKeyBoard(this@WriteActivity, binding?.titleEditText!!.windowToken)
            // 카테고리 스피너 다이얼로그 보이기
            spinnerDialogFragment.show(
                supportFragmentManager,
                "SpinnerDialogFragment"
            )
        }

        // 완료 버튼 -> 게시글 업로드
        binding?.doneBtn?.setOnClickListener {
            hideKeyBoard(this@WriteActivity, binding?.titleEditText!!.windowToken)

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
            vm?.uploadProduct(item, this)

        }

        // Radio Button (드릴게요!, 필요해요!)
        binding?.typeRadioGroup?.setOnCheckedChangeListener(object :
            RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, @IdRes id: Int) {
                hideKeyBoard(this@WriteActivity, binding?.titleEditText!!.windowToken)
                when (id) {
                    R.id.radio_give -> vm?.isGive = true
                    R.id.radio_need -> vm?.isGive = false
                }
            }
        })

        /* 이미지 개수가 달라지면 옵저버가 감지하고 이미지 개수 뷰 갱신 */
        vm?.imageCount?.observe(this, Observer {
            binding?.photoCountText?.text = "${it}/5"
        })

        binding?.backBtn?.setOnClickListener {
            showExitDialog()
        }

    }

    /* Firebase 업로드에 성공한 경우 callback 지정 */
    override fun callbackForSuccessfulUploading(uid: String) {
        vm?.saveProductImage(vm?.imagePaths!!, uid)
        FancyToastUtil(this).showSuccess(getString(R.string.upload_success))
        finish() // 업로드 성공시 작성 액티비티 종료
    }

    /* Firebase 업로드에 실패한 경우 callback 지정 */
    override fun callbackForFailureUploading() {
        FancyToastUtil(this).showFail(getString(R.string.upload_fail))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_CODE.IMAGE_PICKER_REQUEST_CODE -> { // 이미지 피커 :: 갤러리에서 사진을 가져온 경우
                val newData =
                    data?.extras?.getStringArray(GligarPicker.IMAGES_RESULT)!! // 선택한 새로운 이미지 가져오기
                var totalSize: Int = vm?.imageCount?.value!! // 먼저 기존에 저장된 개수 가져오고 그 후에 가져온 개수만큼 더하기

                val tempImgPath = ArrayList<String>() // 전달할 Image path(이미지 경로) arraylist
                //// imgPath.addAll(newData) // image path 받아오기

                for (data in newData) { // 새로운 데이터가 5개를 초과하는지 확인하면서 반복문을 돌림
                    if ((totalSize + 1) > Const.MAX_PHOTO_SIZE) {
                        FancyToastUtil(this).showFail("이미지는 총 5개만 가져올 수 있어요!")
                        break
                    }
                    totalSize += 1 // 전체 선택된 이미지에 개수 더하기
                    tempImgPath.add(data) // 임시 경로 리스트에 추가
                    vm?.imagePaths?.add(data) // 받아온 이미지 경로를 ViewModel의 imagePaths에 추가
                }


                if (!tempImgPath.isNullOrEmpty()) { // 이미지를 한개라도 고른 경우
                    Log.d("WriteActivity", tempImgPath.size.toString())

                    // 새로 추가된 이미지 개수만큼 미리보기 View 추가
                    tempImgPath.forEach { path ->
                        // Photo View
                        try {
                            val photoView = LayoutInflater.from(this)
                                .inflate(R.layout.picked_photo_view, binding?.scrollInnerView, false)
                            Glide.with(this).load(path).into(photoView.photo_image_view)
                            photoView.photo_delete_btn.setOnClickListener {
                                vm?.imagePaths?.remove(path) // ViewModel의 Image Array에서 해당 이미지 원소 제거
                                vm?.imageCount?.postValue(vm?.imageCount?.value!! - 1) // ViewModel의 이미지 개수 값 변경
                                binding?.scrollInnerView?.removeView(photoView) // View 제거

                                // 삭제된 후 남은 이미지 개수 출력
                                Log.d(
                                    "WriteActivity",
                                    "Image count after delete : ${vm?.imagePaths?.size.toString()}"
                                )
                            }
                            binding?.scrollInnerView?.addView(photoView) // Scroll View에 photo view 추가
                        }catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }

                vm?.imageCount?.postValue(totalSize) // 기존 이미지 개수와 새로 가져온 이미지 개수를 합침
            }
        }
    }

    fun showExitDialog() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.write_dialog))
            .setPositiveButton(
                getString(R.string.ok), DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })
            .setNegativeButton(
                getString(R.string.cancel), DialogInterface.OnClickListener { dialog, id -> })
            .create().show()
    }

    override fun onBackPressed() {
        showExitDialog()
    }

}

