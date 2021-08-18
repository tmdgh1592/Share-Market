package com.app.buna.sharemarket.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharemarket.model.LocationItem
import com.app.buna.sharemarket.utils.LocationHelper
import com.app.buna.sharemarket.utils.NetworkStatus
import com.app.buna.sharemarket.view.AddressApiWebView


class InitialViewModel(application: Application, val context: Context, fragment: Fragment) :
    AndroidViewModel(
        application
    ) {

    class Factory(val application: Application, val context: Context, val fragment: Fragment) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return InitialViewModel(application, context, fragment) as T
        }
    }

    val locationItems: MutableLiveData<ArrayList<LocationItem>> =
        MutableLiveData() // 조금씩 담길 로케이션 리스트
    var pages: Int = 1 // 로케이션에 조금씩 담기 위한 변수 (pages * 15)
    val locationList = ArrayList<LocationItem>()
    val locationHelper = LocationHelper(fragment, context)

    init {
        locationItems.value = locationList
    }

    fun getLocationList() {
        Log.d("InitialViewModel", "Location Searching Button Clicked")
        //Log.d("InitialViewModel", "Location : " + locationHelper.getMyLocation()?.toString())
        val list = locationHelper.getMyLocation()
        list?.forEach {
            locationList.add(LocationItem(it.getAddressLine(0)))
        }
        locationItems.value = locationList
    }

    fun startKakaoWebView() {
        /* 인터넷 연결 상태 확인 */
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
    }


}