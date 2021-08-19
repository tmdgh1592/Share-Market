package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.CONST
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.fragment.ThirdInitialFragment
import com.app.buna.sharingmarket.view.InitialActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ThirdInitialViewModel(application: Application, val context: Context, val fragment: ThirdInitialFragment) : AndroidViewModel(application){

    val auth = FirebaseAuth.getInstance()
    private var googleSignInClient : GoogleSignInClient? = null

    class Factory(val application: Application, val context: Context, val fragment: ThirdInitialFragment) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ThirdInitialViewModel(application, context, fragment) as T
        }
    }

    // 확인 버튼 누를 때 호출하는 메소드
    /*fun clickSubmitBtn(phoneNumber: String, editTextCode: EditText, callback: ()->Unit, activty: Activity) {
        val authInstance = PhoneAuth(editTextCode, mAuth, callback, activty)
        authInstance.sendVertificationCode(phoneNumber, activty)
    }*/
    // 확인 버튼 누를 때 호출하는 메소드
    fun clickSubmitBtn() {

    }

    // 구글 로그인 함수
    fun signIn(){
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity() as InitialActivity, gso)

        var signInIntent = googleSignInClient?.signInIntent
        (fragment.requireActivity() as InitialActivity).startActivityForResult(signInIntent, CONST.RC_SIGN_IN)
    }





}