package com.app.buna.sharingmarket.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.buna.sharingmarket.fragment.ThirdInitialFragment
import com.app.buna.sharingmarket.model.items.LocationItem
import com.app.buna.sharingmarket.utils.LocationHelper
import com.app.buna.sharingmarket.repository.PreferenceUtil
import com.app.buna.sharingmarket.view.InitialActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch


class InitialViewModel(application: Application, val context: Context, val view: Fragment) :
    AndroidViewModel(application) {

    class FactoryWithFragment(
        val application: Application,
        val context: Context,
        val view: Fragment
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return InitialViewModel(application, context, view) as T
        }
    }

    val locationItems: MutableLiveData<List<LocationItem>> = MutableLiveData()
    val locationList = ArrayList<LocationItem>()
    val locationHelper = LocationHelper(view, context)

    init {
        locationItems.value = locationList
    }

    fun getLocationList() {
        CoroutineScope(Default).launch {
            Log.d("InitialViewModel", "Location Searching Button Clicked")

            locationList.clear() // 기존 항목 제거

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


    fun startNextFragmentWithSaving(item: LocationItem) {
        PreferenceUtil.putString(context, "jibun", item.location)
        (view.requireActivity() as InitialActivity).replaceFragment(ThirdInitialFragment())
    }



}