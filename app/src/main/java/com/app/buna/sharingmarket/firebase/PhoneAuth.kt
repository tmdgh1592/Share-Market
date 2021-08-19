package com.app.buna.sharingmarket.firebase

import android.app.Activity
import android.util.Log
import android.widget.EditText
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit


class PhoneAuth(val editTextCode: EditText, val mAuth: FirebaseAuth, val callback: () -> Unit, val activity: Activity) {

    lateinit var mVerificationId: String
    lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken


    val callbacks by lazy {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val code: String = credential.smsCode.toString()

                if (code != null) {
                    editTextCode.setText(code)
                    verifyVerificationCode(code)
                }
            }
            override fun onVerificationFailed(e: FirebaseException) {
                //에러 메세지 출력
                Log.d("PhoneAuth", "전화번호 인증 오류 발생")
            }
            override fun onCodeSent(
                verificationId: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;
            }
        }
    }

    fun sendVertificationCode(phoneNumber: String, activity: Activity) {

        Log.d("PhoneAuth", phoneNumber)

        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber("+82"+"1012345678")       // Phone number to verify
            .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyVerificationCode(otp: String) {
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(mVerificationId, otp)

        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        callback() // 전화번호 인증 성공시 콜백함수 호출
                        val user = task?.result?.user
                        Log.d("PhoneAuth", user.toString())
                    } else {
                        //verification unsuccessful.. display an error message
                        var message = "Somthing is wrong, we will fix it soon..."
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid code entered..."
                        }
                    }
                })
    }

}