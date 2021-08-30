package com.app.buna.sharingmarket.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
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
import com.app.buna.sharingmarket.CONST
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.callbacks.FirebaseRepositoryCallback
import com.app.buna.sharingmarket.databinding.ActivityUpdateBinding
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.repository.PreferenceUtil
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.app.buna.sharingmarket.utils.FancyToastUtil
import com.app.buna.sharingmarket.utils.KeyboardUtil.Companion.hideKeyBoard
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.app.buna.sharingmarket.viewmodel.BoardViewModel
import com.app.buna.sharingmarket.viewmodel.WriteViewModel
import com.bumptech.glide.Glide
import com.github.hamzaahmedkhan.spinnerdialog.OnSpinnerOKPressedListener
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerDialogFragment
import com.github.hamzaahmedkhan.spinnerdialog.SpinnerModel
import com.opensooq.supernova.gligar.GligarPicker
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.android.synthetic.main.picked_photo_view.view.*
import org.koin.android.ext.android.get

private var binding: ActivityUpdateBinding? = null

class UpdateActivity : AppCompatActivity(), FirebaseRepositoryCallback {

    val boardVM: BoardViewModel by lazy {
        ViewModelProvider(this, BoardViewModel.Factory(get(), this))
            .get(BoardViewModel::class.java)
    }
    private val writeVM: WriteViewModel by lazy {
        ViewModelProvider(this, WriteViewModel.Factory(get())).get(WriteViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        boardVM?.item = intent.getParcelableExtra<ProductItem>("product_item") // BoardActivity로 부터 전달받은 Product Data
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
        writeVM.category = boardVM.item.category
        val spinnerDialogFragment = SpinnerDialogFragment.newInstance(
            getString(R.string.category), categoryList,
            object : OnSpinnerOKPressedListener {
                override fun onItemSelect(data: SpinnerModel, selectedPosition: Int) {
                    binding?.categoryText?.text = data.text
                    writeVM?.category = (data.text) // 선택한 카테고리를 뷰 모델에 저장
                }

            }, 0
        )
        spinnerDialogFragment.buttonText = getString(R.string.done)
        spinnerDialogFragment.themeColorResId = ContextCompat.getColor(this, R.color.app_green)

        if (boardVM.item.isGive) {
            binding?.radioGive?.isChecked = true
        }else {
            binding?.radioGive?.isChecked = false
        }


        /* 갤러리 포토 피커 */
        binding?.photoAddBtn?.setOnClickListener {
            GligarPicker()
                .requestCode(REQUEST_CODE.IMAGE_PICKER_REQUEST_CODE)
                .withActivity(this)
                .cameraDirect(false) // 열자마자 바로 카메라로 이동할지 여부
                .limit(CONST.MAX_PHOTO_SIZE) // 이미지 선택 개수 제한
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
            } else if (writeVM?.category == null) { // 카테고리가 선택되지 않은 경우
                FancyChocoBar(this).showOrangeSnackBar(getString(R.string.category_empty))
                return@setOnClickListener
            } else if (writeVM?.isGive == null) { // 라디오 버튼을 선택하지 않은 경우
                FancyChocoBar(this).showOrangeSnackBar(getString(R.string.option_empty))
                return@setOnClickListener
            } else if (!NetworkStatus.isConnectedInternet(this)) { // 인터넷 연결이 되어있지 않은 경우
                FancyChocoBar(this).showOrangeSnackBar(getString(R.string.internet_check))
                return@setOnClickListener
            } else if (location == null) { // 지역 데이터를 가져오지 못한 경우
                FancyChocoBar(this).showAlertSnackBar(getString(R.string.upload_fail))
                return@setOnClickListener
            }


            // 기존에 있던 삭제를 위한 파일 이름 리스트는 초기화
            boardVM.item.fileNamesForDelete.clear()
            // write ViewModel의 imageCount만큼 반복해서 추가
            for (i in 1..writeVM?.imageCount.value!!) {
                writeVM?.fileNameForDelete?.add(i.toString())
                boardVM.item.fileNamesForDelete?.add(i.toString()) // 새로 추가
            }

            val item = ProductItem(
                uid = writeVM?.getUid()!!, // 상품 주인 uid
                documentId = boardVM?.item.documentId, // 문서 id
                owner = writeVM?.getUserName()!!,  // 상품 주인
                category = writeVM?.category!!, // 카테고리
                location = location!!, // 지역
                time = boardVM.item.time, // 게시글 업로드 시간
                title = title, // 게시글 제목
                content = content, // 게시글 내용
                likeCount = boardVM?.item.likeCount, // 좋아요 개수
                favorites = boardVM?.item.favorites, // 좋아요 누른 유저들
                isComplete = boardVM?.item.isComplete, // 거래 완료된지 여부
                isGive = writeVM?.isGive!!, // 주는건지 받는건지
                isExchange = boardVM?.item.isExchange, // 무료나눔 Activity이기 때문에 교환은 false 처리
                fileNamesForDelete = writeVM?.fileNameForDelete!! // 파일 삭제를 위한 숫자 이름 리스트
            )

            boardVM?.item = item // 게시글 수정 후 BoardActivity의 item을 업데이트하기 위한 데이터

            // 게시글 데이터 업데이트
            writeVM?.updateProduct(item, this)

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

        // Radio Button (드릴게요!, 필요해요!)
        binding?.typeRadioGroup?.setOnCheckedChangeListener(object :
            RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, @IdRes id: Int) {
                //hideKeyBoard(this@UpdateActivity, binding?.titleEditText!!.windowToken)
                when (id) {
                    R.id.radio_give -> {
                        writeVM?.isGive = true
                    }
                    R.id.radio_need -> {
                        writeVM?.isGive = false
                    }
                }
            }
        })

        /* 이미지 개수가 달라지면 옵저버가 감지하고 이미지 개수 뷰 갱신 */
        writeVM?.imageCount?.observe(this, Observer {
            binding?.photoCountText?.text = "${it}/5"
        })

        binding?.backBtn?.setOnClickListener {
            showExitDialog()
        }


        /*
        * boardVM.item에 수정할 데이터가 들어있음.
        * 그냥 vm과 유의해서 코딩해야함
        * */

        /* Update Activity에 처음 들어왔을 때 기존 사진 보여주기 */
        // WrtieViewModel의 imageCount를 FireStorage에 저장된 이미지 개수로 초기화
        writeVM?.imageCount?.value = boardVM.item.imgPath.size
        boardVM.item.imgPath.values.forEach { storagePath ->
            writeVM?.imagePathHash.put(storagePath, false)
        }

        // 기존 이미지 개수만큼 미리보기 View 추가
        for ((key, path) in boardVM.item.imgPath.entries) {
            val photoView = LayoutInflater.from(this)
                .inflate(R.layout.picked_photo_view, binding?.scrollInnerView, false)
            Glide.with(this).load(path).into(photoView.photo_image_view)
            photoView.photo_delete_btn.setOnClickListener {
                boardVM.item.imgPath.remove(key) // BoardVM의 Storage path를 제거 (기존에 가져온 이미지 path이기 때문에)
                writeVM?.imagePathHash.remove(path)
                writeVM?.imageCount?.postValue(writeVM?.imageCount?.value!! - 1) // ViewModel의 이미지 개수 값도 변경 (Photo Picker에서 사용하기 때문)
                binding?.scrollInnerView?.removeView(photoView) // View 제거
                // 삭제된 후 남은 이미지 개수 출력
                Log.d(
                    "UpdateActivity",
                    "Image count after delete : ${writeVM?.imagePathHash.size}"
                )
            }
            binding?.scrollInnerView?.addView(photoView) // Scroll View에 photo view 추가
        }

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
                var totalSize: Int =
                    writeVM?.imageCount?.value!! // 먼저 기존에 저장된 개수 가져오고 그 후에 가져온 개수만큼 더하기

                Log.d("size", totalSize.toString())
                val tempImgPath = ArrayList<String>() // 전달할 Image path(이미지 경로) arraylist :: 화면에 이미지 리스트를 보여주기 위한 임시 리스트
                // imgPath.addAll(newData) // image path 받아오기

                for (data in newData) { // 새로운 데이터가 5개를 초과하는지 확인하면서 반복문을 돌림
                    if ((totalSize + 1) > CONST.MAX_PHOTO_SIZE) { // MAX_PHOTO_SIZE(5개) 보다 많으면
                        FancyToastUtil(this).showFail("이미지는 총 5개만 가져올 수 있어요!")
                        break
                    }
                    totalSize += 1 // 전체 선택된 이미지에 개수 더하기
                    tempImgPath.add(data) // 임시 경로 리스트에 추가
                    writeVM?.imagePathHash?.put(data, true) // 받아온 이미지 경로를 ViewModel의 imagePathHash에 추가
                }


                if (!tempImgPath.isNullOrEmpty()) { // 이미지를 한개라도 고른 경우
                    Log.d("UpdateActivity", tempImgPath.size.toString())

                    // 새로 추가된 이미지 개수만큼 미리보기 View 추가
                    tempImgPath.forEach { path ->
                        // Photo View
                        val photoView = LayoutInflater.from(this)
                            .inflate(R.layout.picked_photo_view, binding?.scrollInnerView, false)
                        Glide.with(this).load(path).into(photoView.photo_image_view)
                        photoView.photo_delete_btn.setOnClickListener {
                            writeVM?.imagePathHash?.remove(path) // ViewModel의 Image Array에서 해당 이미지 원소 제거
                            writeVM?.imageCount?.postValue(writeVM?.imageCount?.value!! - 1) // ViewModel의 이미지 개수 값 변경
                            binding?.scrollInnerView?.removeView(photoView) // View 제거

                            // 삭제된 후 남은 이미지 개수 출력
                            Log.d(
                                "UpdateActivity",
                                "Image count after delete : ${writeVM?.imagePathHash?.size}"
                            )
                        }
                        binding?.scrollInnerView?.addView(photoView) // Scroll View에 photo view 추가
                    }
                }

                writeVM?.imageCount?.postValue(totalSize) // 기존 이미지 개수와 새로 가져온 이미지 개수를 합침
            }
        }
    }

    fun showExitDialog() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.update_dialog))
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

    override fun callbackForSuccessfulUploading(uid: String) {
        writeVM?.updateProductImage(uid, boardVM?.item.fileNamesForDelete)
        FancyToastUtil(this).showSuccess(getString(R.string.update_success))
        finish() // 업로드 성공시 작성 액티비티 종료
    }

    override fun callbackForFailureUploading() {
        FancyToastUtil(this).showFail(getString(R.string.update_fail))
    }
}