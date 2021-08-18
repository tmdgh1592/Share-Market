package com.app.buna.sharemarket.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.app.buna.sharemarket.CONST.Companion.PERMISSIONS_REQUEST_CODE
import com.app.buna.sharemarket.R
import com.app.buna.sharemarket.fragment.SecondInitialFragment
import org.koin.core.Koin
import java.io.IOException
import java.util.*

class LocationHelper(val fragment: Fragment, val context: Context) {

    private lateinit var locationManager: LocationManager
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private val MAX_RESULT = 100
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // 현재 좌표를 구한 뒤 현 주소명 리스트 반환
    @SuppressLint("ServiceCast", "LongLogTag")
    fun getMyLocation(): List<Address>?{
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(!locationManager.isProviderEnabled(GPS_PROVIDER)){
            FancyChocoBar(fragment.requireActivity()).showAlertSnackBar(context.getString(R.string.gps_check))
            val gpsOptionsIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(gpsOptionsIntent)
            return null
        }

        var userLocation: Location? = getLatLng()
        var mResultList: List<Address>? = null

        if (userLocation != null) {
            lat = userLocation.latitude
            lng = userLocation.longitude

            Log.d("LocationManager :: getLocation()", "현재 위치 값 : ${lat}, ${lng}")

            var mGeoCoder = Geocoder(context, Locale.KOREA)

            try{
                mResultList = mGeoCoder.getFromLocation(
                    lat, lng, 10
                )
            }catch (e: IOException) {
                e.printStackTrace()
            }

            if (mResultList != null) {
                Log.d("LocationManager :: getLocation()", mResultList[0].getAddressLine(0))
                return mResultList
            }
        }
        return null
    }

    // 위치 권한 승인 및 좌표 반환
    private fun getLatLng(): Location? {
        var currentLatLng: Location?

        // FINE_LOCATION : 50m 이내의 정확한 위치 제공
        var hasFineLocationPermission = ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION)
        // COARSE_LOCATION : 1.6km 이내의 위치 제공
        var hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION)

        // 유저가 권한을 승인한 경우
        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            val locationProvider = GPS_PROVIDER
            currentLatLng = locationManager.getLastKnownLocation(locationProvider)
        }else{ // 유저가 권한을 승인하지 않은 경우, 경고 문구 출력 후 재 실행
            if(ActivityCompat.shouldShowRequestPermissionRationale(fragment.requireActivity(), REQUIRED_PERMISSIONS[0])){
                ActivityCompat.requestPermissions(fragment.requireActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            }else{
                ActivityCompat.requestPermissions(fragment.requireActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            }
            // 권한 요청 스낵바 출력
            FancyChocoBar(fragment.requireActivity()).showSnackBar(context.getString(R.string.require_location_permission))
            return null
        }
        return currentLatLng!!
    }

}