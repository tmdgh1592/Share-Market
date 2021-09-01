package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.SOSOCK
import com.app.buna.sharingmarket.fragment.InitialThirdFragment
import com.app.buna.sharingmarket.model.items.LocationItem
import com.app.buna.sharingmarket.utils.LocationHelper
import com.app.buna.sharingmarket.repository.PreferenceUtil
import com.app.buna.sharingmarket.activity.InitialActivity
import com.app.buna.sharingmarket.repository.FirebaseRepository
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.app.buna.sharingmarket.utils.NetworkStatus
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch


class InitialViewModel(application: Application) :
    AndroidViewModel(application) {

    private lateinit var view: Fragment


    //----------- * Fragment에서 생성하는 경우 생성자
    constructor(application: Application, view: Fragment): this(application) {
        this.view = view
    }
    class FactoryWithFragment(
        val application: Application,
        val context: Context,
        val view: Fragment
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return InitialViewModel(application, view) as T
        }
    }
    // Fragment에서 생성하는 경우 -----------* //


    /* Fragment가 아닌 경우 (ex) Activity) */
    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return InitialViewModel(application) as T
        }

    }

    val locationItems: MutableLiveData<List<LocationItem>> = MutableLiveData()
    val locationList = ArrayList<LocationItem>()
    lateinit var locationHelper: LocationHelper
    var mySoSock = MutableLiveData<String>()


    private val firebaseRepository: FirebaseRepository = FirebaseRepository.instance // 파이어베이스 Realtime DB

    init {
        locationItems.value = locationList
        mySoSock.value = ""
    }

    fun getLocationList(context: Context) {
        CoroutineScope(Default).launch {
            Log.d("InitialViewModel", "Location Searching Button Clicked")

            locationList.clear() // 기존 항목 제거
            locationHelper = LocationHelper(view, context) // LocationHelper 객체 생성

            val list = locationHelper.getMyLocation()
            list?.forEach {
                if (it.adminArea != null && it.locality != null && it.thoroughfare != null)
                    locationList.add(LocationItem(it.adminArea + " " + it.locality + " " + it.thoroughfare))
            }
            locationItems.postValue(locationList.distinct())
        }
    }

    /* fun startKakaoWebView() {
         *//* 인터넷 연결 상태 확인 *//*
        val status = NetworkStatus.getConnectivityStatus(getApplication())
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            val intent = Intent(context, AddressApiWebView::class.java)
            context.startActivity(intent)
        } else {
            Toast.makeText(
                context,
                "인터넷 연결을 확인해주세요.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }*/


    fun startNextFragmentWithSaving(context: Context, item: LocationItem) {
        PreferenceUtil.putString(context, "jibun", item.location)
        (view.requireActivity() as InitialActivity).replaceFragment(InitialThirdFragment())
    }

    fun getUserName(): String{
        val userName = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        Log.d("Firebase", userName)
        return userName
    }

    // FourthInitialFragment :: 가입 완료 텍스트 버튼 클릭 수행 로직
    fun register(context: Context) {
        if (mySoSock.value == "") {
            FancyChocoBar(view.requireActivity()).showOrangeSnackBar(context.getString(R.string.sosock_check))
        }

        // 인터넷 연결 상태 체크
        if(!NetworkStatus.isConnectedInternet(context)){
            FancyChocoBar(view.requireActivity()).showSnackBar(context.getString(R.string.internet_check)) // 인터넷 사용 문구 스낵바 출력
            return // 미연결시 아무런 로직 수행하지 않음
        }

        if (mySoSock.value != "" || mySoSock.value != null) {
            // firebase realtimedb에 소속&이름(닉네임) 추가
            PreferenceUtil.putString(context, "sosock", mySoSock.value!!) // SharedPreference에 저장
            Log.d("InitialViewModel", PreferenceUtil.getString(context, "sosock", "")) // Log출력
            
            saveUserInfo(context, key = "jibun", value = "jibun", isInPref = true) // firebase realtimedb에 사용자의 주소 등록
            saveUserInfo(context, key = "sosock", value = "sosock", isInPref = true) // firebaseDB에 유저의 ''이름(닉네임)'' 저장
            saveUserInfo(context, key = "nickname", value = FirebaseAuth.getInstance().currentUser?.displayName.toString()) // firebaseDB에 유저의 ''소속'' 저장

            // 가입 완료 버튼 누르고 문제 없으면 MainActivity 실행
            (view.requireActivity() as InitialActivity).moveMainPage(FirebaseAuth.getInstance().currentUser)
        }
    }

    // FourthInitialFragment :: 소속 이미지 클릭
    // 소속 이미지 클릭시 소속 변경
    fun changeSoSock(sosock: String) {
        when(sosock) {
            SOSOCK.PERSONAL -> mySoSock.value = SOSOCK.PERSONAL
            SOSOCK.AGENCY -> mySoSock.value = SOSOCK.AGENCY
            SOSOCK.COMPANY -> mySoSock.value = SOSOCK.COMPANY
        }
    }

    fun saveUserInfo(context: Context, key: String, value: String, isInPref: Boolean = false) {
        // preference에 저장된 값을 가져와서 저장하려는 경우
        if (isInPref) {
            firebaseRepository.saveUserInfo(key, PreferenceUtil.getString(context, key, ""), true)
            return
        }
        // preference가 아닌 단순 key, value를 firebaseDB에 저장하는 경우
        firebaseRepository.saveUserInfo(key, value, true)
    }

}