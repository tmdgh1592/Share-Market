package com.app.buna.sharingmarket.repository

import android.util.Log
import com.app.buna.sharingmarket.callbacks.FirebaseRepositoryCallback
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository {

    companion object {
        // Firebase Realtime DB instance 싱글톤 생성
        val firebaseDatabaseinstance = FirebaseDatabase.getInstance()
        val firebaseStoreInstance = FirebaseFirestore.getInstance()
        // FirebaseDB 싱글톤 생성
        val instance = FirebaseRepository()
    }

    // 유저 정보에 대한 데이터를 저장하는 메소드
    fun saveUserInfo(key: String, data: String, update: Boolean) {
        // realtimeDB
        // 유저 정보 Reference
        if(update) { // 값을 갱신해야 하는 경우 (update == ture)
            firebaseDatabaseinstance.getReference("users") // /user_info
                .child(FirebaseAuth.getInstance().currentUser!!.uid) // /uid
                .child(key) //
                .setValue(data)
        }else { // 값을 누적해야 하는 경우 (update == ture)
            firebaseDatabaseinstance.getReference("users") // /user_info
                .child(FirebaseAuth.getInstance().currentUser!!.uid) // /uid
                .child(key) //
                .push()
                .setValue(data)

        }
    }


    // 파이어베이스 DB는 비동기로 실행되기 때문에 infoData 변수가 초기화되지 않을 수도 있음
    // 이 부분 수정해줘야 함
    fun getUserInfo(uid: String, key: String): String? {

        var infoData: String? = null

        firebaseDatabaseinstance.getReference("users").child(uid).child(key).addListenerForSingleValueEvent(
            object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = snapshot.getValue(String::class.java)
                    Log.d("FirebaseRepository", data)
                    infoData = data
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("FirebaseRepository", "Canceled")
                }
            }
        )
        return infoData
    }

    // 파이어 스토어에 제품 게시글 등록
    fun uploadBoard(product: ProductItem, callback: FirebaseRepositoryCallback) {
        firebaseStoreInstance.collection("Boards")
            .add(product)
            .addOnSuccessListener {
                saveUserInfo("board_uid", it.id, false) // 성공시 realtimeDB의 유저 데이터 목록에 해당 게시글 id 저장
                callback.callbackForSuccessfulUploading() // MainHomeFragment -> 성공 callback
            }.addOnFailureListener {
                callback.callbackForFailureUploading() // MainHomeFragment -> 성공 callback
            }
    }

}