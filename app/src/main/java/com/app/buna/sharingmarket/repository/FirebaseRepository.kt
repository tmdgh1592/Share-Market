package com.app.buna.sharingmarket.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseRepository {

    // 유저 정보 Reference
    val userInfoRef: DatabaseReference = FirebaseDatabaseinstance.getReference("user_info")


    companion object {
        // Firebase Realtime DB instance 싱글톤 생성
        val FirebaseDatabaseinstance = FirebaseDatabase.getInstance()
        // FirebaseDB 싱글톤 생성
        val instance = FirebaseRepository()
    }

    // 유저 정보에 대한 데이터를 저장하는 메소드
    fun saveUserInfo(key: String, data: String) {
        userInfoRef // /user_info
            .child(FirebaseAuth.getInstance().currentUser!!.uid) // /uid
            .child(key) //
            .setValue(data)
    }

}