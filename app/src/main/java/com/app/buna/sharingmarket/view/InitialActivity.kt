package com.app.buna.sharingmarket.view

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.CONST
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.fragment.ThirdInitialFragment
import com.app.buna.sharingmarket.viewmodel.ThirdInitialViewModel
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.android.ext.android.get

class InitialActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        auth = FirebaseAuth.getInstance() // facebook auth 인스턴스 생성
        replaceFragment(ThirdInitialFragment()) // 처음 켰을 때 맨 처음 프래그먼트 화면 띄우기

    }

    // 프래그먼트 전환하는 메소드
    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction
            .setCustomAnimations(
                R.anim.anim_fade_in,
                R.anim.anim_fade_out
            ) // 프래그먼트 전환시 보여질 애니메이션 설정
            .replace(R.id.initial_frame_layout, fragment).commit() // 해당 프래그먼트 실행
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // facebook login 화면 닫힐 때 들어오는 콜백


        if(callbackManager != null) {
            Log.d("RESULT", callbackManager?.toString())
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
        // 다음 주소에서 주소 선택했을 때 :: AddressApiWebView
        if (requestCode == CONST.API_COMPLETED_FINISH) {
            replaceFragment(ThirdInitialFragment())
        } else if (requestCode == CONST.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("InitialActivty", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("InitialActivty", "Google sign in failed", e)
            }
        }

    }

    // 구글 로그인 후 메인 액티비티로 이동
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("InitialActivty", "signInWithCredential:success")
                    val user = auth?.currentUser
                    moveMainPage(user)
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("InitialActivty", "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
    }

    // 유저정보 넘겨주고 메인 액티비티 호출
    fun moveMainPage(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            finish()
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        }
    }

    override fun onStart() {
        super.onStart()
        // 로그인 되어 있는 상태이면 바로 메인액티비티 이동
        moveMainPage(auth?.currentUser)
    }


}