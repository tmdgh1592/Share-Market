package com.app.buna.sharingmarket.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.fragment.initial.InitialFirstFragment
import com.app.buna.sharingmarket.fragment.initial.InitialFourthFragment
import com.app.buna.sharingmarket.fragment.initial.InitialThirdFragment
import com.app.buna.sharingmarket.repository.Local.PreferenceUtil
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class InitialActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)

        auth = FirebaseAuth.getInstance() // facebook auth 인스턴스 생성
        startLastestProgressFragment() // 가장 마지막까지 진행한 프래그먼트 띄우기

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

    @JvmOverloads
    fun replaceFragment(fragment: Fragment, user:FirebaseUser){
        if(user != null){
            val fragmentTransaction = supportFragmentManager.beginTransaction()

            fragmentTransaction
                .setCustomAnimations(
                    R.anim.anim_fade_in,
                    R.anim.anim_fade_out
                ) // 프래그먼트 전환시 보여질 애니메이션 설정
                .replace(R.id.initial_frame_layout, fragment).commit() // 해당 프래그먼트 실행
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // facebook login 화면 닫힐 때 들어오는 콜백
        if(callbackManager != null) {
            Log.d("RESULT", callbackManager?.toString())
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
        // 다음 주소에서 주소 선택했을 때 :: AddressApiWebView
        if (requestCode == REQUEST_CODE.API_COMPLETED_FINISH) {
            var jibun: String? = data?.getStringExtra("jibun")

            if (jibun != null && jibun != "") {
                replaceFragment(InitialThirdFragment())
            }
        } else if (requestCode == REQUEST_CODE.RC_SIGN_IN) {
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
                    Log.d("InitialActivty", "signInWithCredential:success")
                    PreferenceUtil.putString(this, key = "nickname", value = FirebaseAuth.getInstance().currentUser?.displayName.toString()) // Preference에 유저의 '(닉네임)' 저장
                    val user = auth?.currentUser
                    replaceFragment(InitialFourthFragment(), user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("InitialActivty", "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
    }

    // 유저정보 넘겨주고 메인 액티비티 호출
    fun moveMainPage(user: FirebaseUser?) {

        val sosock = PreferenceUtil.getString(this, "sosock", "")
        val page = PreferenceUtil.getInt(this, "fragment_page", 0)

        if (user != null && sosock != "" && page == -1 && Firebase.auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // 로그인 되어 있는 상태이고 소속을 입력했다면 바로 메인 액티비티로 이동
        moveMainPage(auth?.currentUser)

        //replaceFragment(InitialFirstFragment())
    }

    fun startLastestProgressFragment() {
        val fragmentPage = PreferenceUtil.getInt(this, "fragment_page")
        when (fragmentPage) {
            0, 1 -> replaceFragment(InitialFirstFragment()) // 위치 설정 이전 까지 진행한 경우
            2 -> replaceFragment(InitialThirdFragment()) // 로그인&회원가입 까지 진행한 경우
            3 -> replaceFragment(InitialFourthFragment()) // 소속 정하는 곳 까지 진행한 경우
            -1 -> { // MainPage까지 이동한 경우는 -1
                startActivity(
                    Intent(this, MainActivity::class.java),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
                )
                finish()
            }
            else -> replaceFragment(InitialFirstFragment()) // 그렇지 않은 경우 처음부터 시작
        }
    }


}