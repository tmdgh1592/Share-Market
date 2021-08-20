package com.app.buna.sharingmarket.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.CONST
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.fragment.ThirdInitialFragment
import com.app.buna.sharingmarket.view.InitialActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ThirdInitialViewModel(application: Application, val context: Context) : AndroidViewModel(application){

    lateinit var view: InitialActivity

    constructor(application: Application, context: Context, view: InitialActivity) : this(application, context){
        this.view = view
    }

    val auth = FirebaseAuth.getInstance()
    private var googleSignInClient : GoogleSignInClient? = null
    var callbackManager: CallbackManager? = null
    val TAG = "Login"

    class Factory(val application: Application, val context: Context, val view: InitialActivity) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ThirdInitialViewModel(application, context, view) as T
        }
    }

    // Activity에서 viewmodel 생성시 사용
    // callbackmanager 가져오기 위함
    class FactoryWithNoFragment(val application: Application, val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ThirdInitialViewModel(application, context) as T
        }
    }

    // 확인 버튼 누를 때 호출하는 메소드
    /*fun clickSubmitBtn(phoneNumber: String, editTextCode: EditText, callback: ()->Unit, activty: Activity) {
        val authInstance = PhoneAuth(editTextCode, mAuth, callback, activty)
        authInstance.sendVertificationCode(phoneNumber, activty)
    }*/

    // 구글 로그인 함수
    fun signInGoogle(){
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(view, gso)

        var signInIntent = googleSignInClient?.signInIntent
        view.startActivityForResult(signInIntent, CONST.RC_SIGN_IN)
    }

    @SuppressLint("LongLogTag")
    fun signInFacebook() {
        callbackManager = CallbackManager.Factory.create()
        view.callbackManager = callbackManager!! // view(InitialActivty)로 callback manager 전달
        Log.d("ThirdInitialViewModel -> callbackManager Id", callbackManager.toString())

        val getData = listOf("public_profile","email") // facebook 로그인 시 가져올 데이터
        LoginManager.getInstance().logInWithReadPermissions(view, getData)
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    handleFBToken(result?.accessToken) // 토큰을 성공적으로 받아오면 해당 메소드 실행
                }
                override fun onCancel() {
                    Log.d("ThirdInitialViewModel", "Login Canceled")
                }
                override fun onError(error: FacebookException?) {}
            })
    }

    fun handleFBToken(token : AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!) // Token을 넘기고 Credential을 가져옴

        // credential로 로그인 시도
        auth?.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // 로그인 성공시 user 정보 가져오고
                val user = auth!!.currentUser
                Log.d(TAG, "로그인 성공 : ${user?.displayName}")
                view.moveMainPage(user) // View에게 메인 액티비티로 이동하도록 요청
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.exception)
            }
        }
    }




}