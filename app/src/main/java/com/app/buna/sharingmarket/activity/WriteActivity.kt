package com.app.buna.sharingmarket.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.callbacks.FirebaseRepositoryCallback
import com.app.buna.sharingmarket.databinding.ActivityWriteBinding
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.app.buna.sharingmarket.viewmodel.WriteViewModel

private var binding: ActivityWriteBinding? = null
private var vm: WriteViewModel? = null



class WriteActivity : AppCompatActivity(), FirebaseRepositoryCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()

    }

    private fun initBinding() {
        vm = ViewModelProvider(this, WriteViewModel.Factory(application)).get(WriteViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_write)
        binding?.lifecycleOwner =  this
        binding?.viewModel = vm
    }

    /* Firebase 업로드에 성공한 경우 callback 지정 */
    override fun callbackForSuccessfulUploading() {
        FancyChocoBar(this).showSnackBar(getString(R.string.upload_success))
    }
    /* Firebase 업로드에 실패한 경우 callback 지정 */
    override fun callbackForFailureUploading() {
        FancyChocoBar(this).showAlertSnackBar(getString(R.string.upload_fail))
    }

}