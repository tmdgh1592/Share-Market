package com.app.buna.sharingmarket.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.REQUEST_CODE
import com.app.buna.sharingmarket.activity.BoardActivity
import com.app.buna.sharingmarket.activity.MainActivity
import com.app.buna.sharingmarket.callbacks.IFirebaseGetStoreDataCallback
import com.app.buna.sharingmarket.callbacks.ILogoutCallback
import com.app.buna.sharingmarket.model.CategoryItem
import com.app.buna.sharingmarket.model.BoardItem
import com.app.buna.sharingmarket.repository.Firebase.FirebaseRepository
import com.app.buna.sharingmarket.repository.Local.PreferenceUtil
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainViewModel(application: Application, val context: Context) :
    AndroidViewModel(application) {
    class Factory(val application: Application, val context: Context) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(application, context) as T
        }
    }

    val productItems = MutableLiveData<ArrayList<BoardItem>>(ArrayList())
    var surveyOptionId: Int? = null // 설문조사 radio 옵션
    var surveyText: String? = null // 설문조사 문구



    // View Model의 product
    fun getProductData(category: String, callback: IFirebaseGetStoreDataCallback) {
        FirebaseRepository.instance.getBoardData(category, callback)
    }

    fun getBoardByKeyword(keyword: String, callback: IFirebaseGetStoreDataCallback) {
        FirebaseRepository.instance.getBoardByKeyword(keyword, callback)
    }

    fun clickProduct(position: Int) {
        val intent = Intent(context, BoardActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra("product_item", productItems.value!!.get(position))
        (context as MainActivity).startActivityForResult(intent, REQUEST_CODE.REFRESH_MAIN_HOME_FRAGMENT_CODE)
    }

    @SuppressLint("ResourceType")
    fun getCategoryList(): ArrayList<CategoryItem> {
        val list = ArrayList<CategoryItem>()
        val categoriesTitle = context.resources.getStringArray(R.array.category_title)
        val categoriesDrawables = context.resources.obtainTypedArray(R.array.category_res_id)

        for (i in categoriesTitle.indices) {
            list.add(CategoryItem(categoriesTitle[i], categoriesDrawables.getResourceId(i, 0)))
        }
        return list
    }

    fun getUserName(): String = PreferenceUtil.getString(context, "nickname")
    fun getJibun(): String = PreferenceUtil.getString(context, "jibun")

    // 로그아웃
    fun logout(logoutCallback: ILogoutCallback) {
        if (NetworkStatus.isConnectedInternet(context)) {
            Firebase.auth.signOut() // 로그아웃
            PreferenceUtil.putInt(context, "fragment_page", 0) // 현재까지 진행한 fragment_page를 초기화면으로 돌림
            logoutCallback.success()
            Log.d("MainViewModel", "Logout Success")
        }else {
            logoutCallback.fail()
            Log.d("MainViewModel", "Logout Fail")
        }
    }

    // 회원탈퇴
    @SuppressLint("LongLogTag")
    fun unregister(cause: String, id: Int, activityFinishCallback: () -> Unit) {
        // DB에 탈퇴 사유 저장
        FirebaseRepository.instance.saveUnregisterCause(cause, id) { // DB에 저장을 완료하였다면
            // 파이어베이스에서 유저 delete
            Firebase.auth.currentUser!!.delete().addOnCompleteListener {
                Log.d("MainViewModel", "User account deleted")
                PreferenceUtil.putInt(context, "push", 0) // 토큰 값 0으로 설정
                PreferenceUtil.putInt(context, "fragment_page", 0) // 현재까지 진행한 fragment_page를 초기화면으로 돌림
                PreferenceUtil.putInt(context, "push", 0) // 푸시 값으로 초기화
                activityFinishCallback() // 액티비티 종료 콜백
            }.addOnFailureListener { exception ->
                Log.d("MainViewModel Unregister Error", exception.message)
            }
        }
    }

    fun saveProfile(imgUri: Uri, callback: () -> Unit) {
        FirebaseRepository.instance.saveProfile(imgUri, callback)
    }

    fun getProfile(uid: String, callback: (String?)->Unit) {
        FirebaseRepository.instance.getProfile(uid, callback)
    }

    fun saveProfileUriInPref(uri: Uri) {
        PreferenceUtil.putString(context, "profile_uri", uri.toString())
        Log.d("mytest",getProfileUriInPref())
    }

    fun getProfileUriInPref(): String{
        return PreferenceUtil.getString(context, "profile_uri")
    }

    fun saveLocationInFirebase(jibun: String) = FirebaseRepository.instance.saveUserInfo("jibun", jibun, true)
    fun saveLocationInPref(jibun: String) = PreferenceUtil.putString(context, "jibun", jibun)

    // 푸시토큰 발급
    fun registerPushToken() {
        var uid = Firebase.auth.uid
        var tokenMap = mutableMapOf<String, Any>()
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                tokenMap["pushtoken"] =  token
                FirebaseRepository.instance.registerToken(uid, tokenMap)
                PreferenceUtil.putInt(context, "push", 1)
            }

        }
    }

}