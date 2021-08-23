package com.app.buna.sharingmarket.repository

import android.net.Uri
import android.util.Log
import com.app.buna.sharingmarket.callbacks.FirebaseRepositoryCallback
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.utils.FancyChocoBar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File

class FirebaseRepository {

    companion object {
        // FirebaseDB 싱글톤 생성
        val instance = FirebaseRepository()

        // Firebase Realtime DB instance 싱글톤 생성
        val firebaseDatabaseinstance = FirebaseDatabase.getInstance()

        // Firebase Store instacne 싱글톤 생성
        val firebaseStoreInstance = FirebaseFirestore.getInstance()

        // Firebase Storage instacne 싱글톤 생성
        val firebaseImgStorage = FirebaseStorage.getInstance()
    }

    // 유저 정보에 대한 데이터를 저장하는 메소드
    fun saveUserInfo(key: String, data: String, update: Boolean) {
        // realtimeDB
        // 유저 정보 Reference
        if (update) { // 값을 갱신해야 하는 경우 (update == ture)
            firebaseDatabaseinstance.getReference("users") // /user_info
                .child(FirebaseAuth.getInstance().currentUser!!.uid) // /uid
                .child(key) //
                .setValue(data)
        } else { // 값을 누적해야 하는 경우 (update == ture)
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

        firebaseDatabaseinstance.getReference("users").child(uid).child(key)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
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
                callback.callbackForSuccessfulUploading(it.id) // MainHomeFragment -> 성공 callback
            }.addOnFailureListener {
                callback.callbackForFailureUploading() // MainHomeFragment -> 성공 callback
            }
    }

    fun saveProductImg(imgPath: ArrayList<String>, boardUid: String) {
        var num = 0
        val storageReference =
            firebaseImgStorage.getReferenceFromUrl("gs://sharing-market.appspot.com")
                .child("/product_images/${boardUid}/")

        // 주의 사항 :: 맨 끝에 있는 child는 파일 명임
        imgPath.forEach { path ->
            val uri: Uri = Uri.fromFile(File(path))
            Log.d("FirebaseRepository", uri.toString())
            val uploadTask: UploadTask = storageReference.child(num.toString()).putFile(uri) // 이미지 파일명을 num으로 지정

            uploadTask.addOnSuccessListener { // 스토리지에 정상적으로 이미지를 저장한 경우
                Log.d("FirebaseRepository", "Successful for uploading image")
            }.addOnFailureListener { // 스토리지에 이미지 저장을 실패한 경우
                Log.d("FirebaseRepository", "Failure for uploading image")
            }
            num += 1
        }

    }

}