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
import com.app.buna.sharingmarket.Const
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.callbacks.IFirebaseRepositoryCallback
import com.app.buna.sharingmarket.databinding.ActivityUpdateBinding
import com.app.buna.sharingmarket.model.main.BoardItem
import com.app.buna.sharingmarket.repository.Local.PreferenceUtil
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

class UpdateActivity : AppCompatActivity(), IFirebaseRepositoryCallback {

    val boardVM: BoardViewModel by lazy {
        ViewModelProvider(this, BoardViewModel.Factory(get(), this))
            .get(BoardViewModel::class.java)
    }
    private val writeVM: WriteViewModel by lazy {
        ViewModelProvider(this, WriteViewModel.Factory(get())).get(WriteViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        boardVM?.item = intent.getParcelableExtra<BoardItem>("product_item") // BoardActivity??? ?????? ???????????? Product Data
        initBinding()
        initView()

    }

    fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update)
        binding?.lifecycleOwner = this
        binding?.viewModel = boardVM
    }

    fun initView() {

        // ???????????? ???????????? ??????
        writeVM?.isGive = boardVM?.item.isGive

        // ???????????? Spinner ????????? ?????? ?????????
        val categoryList = ArrayList<SpinnerModel>()
        resources.getStringArray(R.array.category_title).forEach {
            categoryList.add(SpinnerModel(it))
        }

        // Init ????????? Fragment
        writeVM.category = boardVM.item.category
        var spinnerDialogFragment: SpinnerDialogFragment? = null
        try {
            spinnerDialogFragment = SpinnerDialogFragment.newInstance(
                getString(R.string.category), categoryList,
                object : OnSpinnerOKPressedListener {
                    override fun onItemSelect(data: SpinnerModel, selectedPosition: Int) {
                        binding?.categoryText?.text = data.text
                        writeVM?.category = (data.text) // ????????? ??????????????? ??? ????????? ??????
                    }

                }, 0
            )
            spinnerDialogFragment.buttonText = getString(R.string.done)
            spinnerDialogFragment.themeColorResId = ContextCompat.getColor(this, R.color.app_green)
        }catch (e: IllegalStateException) {
            print(e.message)
        }
        // ???????????? ????????? ???????????????
        binding?.categorySpinner?.setOnClickListener {
            // ???????????? ????????? ????????? ????????? ??????
            hideKeyBoard(this@UpdateActivity, binding?.titleEditText!!.windowToken)
            // ???????????? ????????? ??????????????? ?????????
            spinnerDialogFragment?.show(
                supportFragmentManager,
                "SpinnerDialogFragment"
            )
        }

        /* ????????? ?????? ?????? */
        binding?.photoAddBtn?.setOnClickListener {
            GligarPicker()
                .requestCode(REQUEST_CODE.IMAGE_PICKER_REQUEST_CODE)
                .withActivity(this)
                .cameraDirect(false) // ???????????? ?????? ???????????? ???????????? ??????
                .limit(Const.MAX_PHOTO_SIZE) // ????????? ?????? ?????? ??????
                .show()
        }



        // ?????? ?????? -> ????????? ??????
        binding?.doneBtn?.setOnClickListener {
            hideKeyBoard(this@UpdateActivity, binding?.titleEditText!!.windowToken)

            val location = PreferenceUtil.getString(this, "jibun", "null")
            val title = binding?.titleEditText?.text.toString()
            val content = binding?.contentEditText?.text.toString()

            if (title.isEmpty()) { // ????????? ?????? ?????? ??????
                FancyToastUtil(this).showWarning(getString(R.string.title_empty))
                return@setOnClickListener
            } else if (title.length < 2) { // ?????? ????????? 2?????? ????????? ??????
                FancyToastUtil(this).showWarning(getString(R.string.title_not_enough))
                return@setOnClickListener
            } else if (content.isEmpty()) { // ????????? ?????? ?????? ??????
                FancyToastUtil(this).showWarning(getString(R.string.content_empty))
                //FancyChocoBar(this).showOrangeSnackBar(getString(R.string.content_empty))
                return@setOnClickListener
            } else if (content.length < 10) { // ????????? ???????????? ?????? ??????
                FancyToastUtil(this).showWarning(getString(R.string.content_not_enough))
                return@setOnClickListener
            } else if (writeVM?.category == null) { // ??????????????? ???????????? ?????? ??????
                FancyToastUtil(this).showWarning(getString(R.string.category_empty))
                return@setOnClickListener
            } else if (writeVM?.isGive == null) { // ????????? ????????? ???????????? ?????? ??????
                FancyToastUtil(this).showWarning(getString(R.string.option_empty))
                return@setOnClickListener
            } else if (!NetworkStatus.isConnectedInternet(this)) { // ????????? ????????? ???????????? ?????? ??????
                FancyToastUtil(this).showWarning(getString(R.string.internet_check))
                return@setOnClickListener
            } else if (location == null) { // ?????? ???????????? ???????????? ?????? ??????
                FancyToastUtil(this).showRed(getString(R.string.upload_fail))
                return@setOnClickListener
            }


            // ????????? ?????? ????????? ?????? ?????? ?????? ???????????? ?????????
            boardVM.item.fileNamesForDelete.clear()
            // write ViewModel??? imageCount?????? ???????????? ??????
            for (i in 1..writeVM?.imageCount.value!!) {
                writeVM?.fileNameForDelete?.add(i.toString())
                boardVM.item.fileNamesForDelete?.add(i.toString()) // ?????? ??????
            }

            val item = BoardItem(
                uid = writeVM?.getUid()!!, // ?????? ?????? uid
                documentId = boardVM?.item.documentId, // ?????? id
                owner = writeVM?.getUserName()!!,  // ?????? ??????
                category = writeVM?.category!!, // ????????????
                location = location!!, // ??????
                time = boardVM.item.time, // ????????? ????????? ??????
                title = title, // ????????? ??????
                content = content, // ????????? ??????
                likeCount = boardVM?.item.likeCount, // ????????? ??????
                favorites = boardVM?.item.favorites, // ????????? ?????? ?????????
                isComplete = boardVM?.item.isComplete, // ?????? ???????????? ??????
                isGive = writeVM?.isGive!!, // ???????????? ????????????
                isExchange = boardVM?.item.isExchange, // ???????????? Activity?????? ????????? ????????? false ??????
                fileNamesForDelete = writeVM?.fileNameForDelete!! // ?????? ????????? ?????? ?????? ?????? ?????????
            )

            boardVM?.item = item // ????????? ?????? ??? BoardActivity??? item??? ?????????????????? ?????? ?????????

            // ????????? ????????? ????????????
            writeVM?.updateProduct(item, this)

        }


        // Radio Button (????????????!, ????????????!)
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




        /* ????????? ????????? ???????????? ???????????? ???????????? ????????? ?????? ??? ?????? */
        writeVM?.imageCount?.observe(this, Observer {
            binding?.photoCountText?.text = "${it}/5"
        })

        binding?.backBtn?.setOnClickListener {
            showExitDialog()
        }


        /*
        * boardVM.item??? ????????? ???????????? ????????????.
        * ?????? vm??? ???????????? ???????????????
        * */

        /* Update Activity??? ?????? ???????????? ??? ?????? ?????? ???????????? */
        // WrtieViewModel??? imageCount??? FireStorage??? ????????? ????????? ????????? ?????????
        writeVM?.imageCount?.value = boardVM.item.imgPath.size
        boardVM.item.imgPath.values.forEach { storagePath ->
            writeVM?.imagePathHash.put(storagePath, false)
        }

        // ?????? ????????? ???????????? ???????????? View ??????
        for ((key, path) in boardVM.item.imgPath.entries) {
            val photoView = LayoutInflater.from(this)
                .inflate(R.layout.picked_photo_view, binding?.scrollInnerView, false)
            Glide.with(this).load(path).into(photoView.photo_image_view)
            photoView.photo_delete_btn.setOnClickListener {
                boardVM.item.imgPath.remove(key) // BoardVM??? Storage path??? ?????? (????????? ????????? ????????? path?????? ?????????)
                writeVM?.imagePathHash.remove(path)
                writeVM?.imageCount?.postValue(writeVM?.imageCount?.value!! - 1) // ViewModel??? ????????? ?????? ?????? ?????? (Photo Picker?????? ???????????? ??????)
                binding?.scrollInnerView?.removeView(photoView) // View ??????
                // ????????? ??? ?????? ????????? ?????? ??????
                Log.d(
                    "UpdateActivity",
                    "Image count after delete : ${writeVM?.imagePathHash.size}"
                )
            }
            binding?.scrollInnerView?.addView(photoView) // Scroll View??? photo view ??????
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }

        when (requestCode) {
            REQUEST_CODE.IMAGE_PICKER_REQUEST_CODE -> { // ????????? ?????? :: ??????????????? ????????? ????????? ??????
                val newData =
                    data?.extras?.getStringArray(GligarPicker.IMAGES_RESULT)!! // ????????? ????????? ????????? ????????????
                var totalSize: Int =
                    writeVM?.imageCount?.value!! // ?????? ????????? ????????? ?????? ???????????? ??? ?????? ????????? ???????????? ?????????

                Log.d("size", totalSize.toString())
                val tempImgPath = ArrayList<String>() // ????????? Image path(????????? ??????) arraylist :: ????????? ????????? ???????????? ???????????? ?????? ?????? ?????????
                // imgPath.addAll(newData) // image path ????????????

                for (data in newData) { // ????????? ???????????? 5?????? ??????????????? ??????????????? ???????????? ??????
                    if ((totalSize + 1) > Const.MAX_PHOTO_SIZE) { // MAX_PHOTO_SIZE(5???) ?????? ?????????
                        FancyToastUtil(this).showRed("???????????? ??? 5?????? ????????? ??? ?????????!")
                        break
                    }
                    totalSize += 1 // ?????? ????????? ???????????? ?????? ?????????
                    tempImgPath.add(data) // ?????? ?????? ???????????? ??????
                    writeVM?.imagePathHash?.put(data, true) // ????????? ????????? ????????? ViewModel??? imagePathHash??? ??????
                }


                if (!tempImgPath.isNullOrEmpty()) { // ???????????? ???????????? ?????? ??????
                    Log.d("UpdateActivity", tempImgPath.size.toString())

                    // ?????? ????????? ????????? ???????????? ???????????? View ??????
                    tempImgPath.forEach { path ->
                        // Photo View
                        val photoView = LayoutInflater.from(this)
                            .inflate(R.layout.picked_photo_view, binding?.scrollInnerView, false)
                        Glide.with(this).load(path).into(photoView.photo_image_view)
                        photoView.photo_delete_btn.setOnClickListener {
                            writeVM?.imagePathHash?.remove(path) // ViewModel??? Image Array?????? ?????? ????????? ?????? ??????
                            writeVM?.imageCount?.postValue(writeVM?.imageCount?.value!! - 1) // ViewModel??? ????????? ?????? ??? ??????
                            binding?.scrollInnerView?.removeView(photoView) // View ??????

                            // ????????? ??? ?????? ????????? ?????? ??????
                            Log.d(
                                "UpdateActivity",
                                "Image count after delete : ${writeVM?.imagePathHash?.size}"
                            )
                        }
                        binding?.scrollInnerView?.addView(photoView) // Scroll View??? photo view ??????
                    }
                }

                writeVM?.imageCount?.postValue(totalSize) // ?????? ????????? ????????? ?????? ????????? ????????? ????????? ??????
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
        FancyToastUtil(this).showGreen(getString(R.string.update_success))
        setResult(RESULT_OK)
        finish() // ????????? ????????? ?????? ???????????? ??????
    }

    override fun callbackForFailureUploading() {
        FancyToastUtil(this).showRed(getString(R.string.update_fail))
    }
}