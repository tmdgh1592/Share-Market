package com.app.buna.sharingmarket.repository

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.app.buna.sharingmarket.callbacks.FirebaseGetStorageDataCallback
import com.app.buna.sharingmarket.callbacks.FirebaseRepositoryCallback
import com.app.buna.sharingmarket.model.items.ProductItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
        val firebaseStorage = FirebaseStorage.getInstance()
        val productList = ArrayList<ProductItem>()
    }

    var imgPathCnt = 0

    // Realtime DB에 유저 정보 데이터를 저장하는 메소드
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

    // Realtime DB에 상품 정보에 대한 데이터를 저장하는 메소드
    @SuppressLint("LongLogTag")
    fun saveImgPath(key: String, uri: Uri) {
        // realtimeDB
        // 상품 정보 Reference
        firebaseDatabaseinstance.getReference("products") // product 정보
            .child("img_path")
            .child(key) // 게시글 Uid
            .push()
            .setValue(uri.toString()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(
                        "FirebaseRepository -> saveImgPath",
                        "Successful Save ImgPath in 'img_path' Realtime Database\n${uri}"
                    )
                }
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
                //saveImgPath(it.id, )
                callback.callbackForSuccessfulUploading(it.id) // MainHomeFragment -> 성공 callback
            }.addOnFailureListener {
                callback.callbackForFailureUploading() // MainHomeFragment -> 실패 callback
            }
    }

    // Firebase Storage에 이미지 저장
    fun saveProductImg(
        imgPath: ArrayList<String>,
        boardUid: String
    ) { // boardUid는 FireStore의 랜덤 push값
        var num = 0
        val storageReference =
            firebaseStorage.getReferenceFromUrl("gs://sharing-market.appspot.com")
                .child("/product_images").child("/${boardUid}/")
                .child(num.toString()) // 파이어 스토리지의 상품 이미지 경로

        // 주의 사항 :: 맨 끝에 있는 child는 파일 명임
        imgPath.forEach { path ->
            val uri: Uri = Uri.fromFile(File(path))
            Log.d("FirebaseRepository", "local path : ${uri}") // 기기에 저장되어 있는 이미지 파일 경로


            storageReference.putFile(uri).addOnCompleteListener { task ->
                if (task.isSuccessful) { // Storage에 이미지를 성공적으로 저장했다면
                    storageReference.downloadUrl.addOnSuccessListener { uri ->
                        saveImgPath(boardUid, uri) // 이미지 uri를 RealtimeDB에도 저장 (빠르게 가져오기 위함)
                    }
                }
            }
            num += 1
        }

    }





    /*
    * Logic ::
    * - Firebase는 비동기로 데이터를 처리하기 때문에 Listner를 통해서 작업이 완료된 후에 complete 메소드를 호출해야 함.
    * - Storage에 접근해서 이미지를 하나하나 가져오기엔 속도가 느려 데이터를 대부분 가져오지 못함.
    * - 따라서, Realtime Database에 products/img_path/{board_uid}에 이미지 링크를 저장.
    * - 제품 데이터를 가져올 때 getProductData(), 먼저 FireStore에 접근해서 게시글 데이터를 가져오고,
    * - 데이터를 가져왔다면 (get().addOnCompleteListner -> task.isSuccessful), 게시글 개수만큼 반복문 수행
    * - 반복할 때, Database에 저장되어 있는 자식 이미지 url들을 child(document.id).get()으로 가져옴.
    * - OnSuccessfulListner가 실행되면 null 체크를 통해 이미지 url 유무 확인.
    * - item의 HashMap 필드 변수에 urlMap 저장
    * - 이미지를 다 가져오면 (boardCount == task.getResult().size()) = (가져온 게시글 개수 == 전체 게시글 개수) callback() 호출
    * - MainHomeFragment의 RecyclerView와 ViewModel에 가져온 데이터로 업데이트
    * */

    // *** Firebase 딜레이를 고려해서 Listener로 완료된 것을 항상 계산하고 코딩해야함. ***

    // Firebase 스토리지에서 게시글을 가져오기
    fun getProductData(callback: FirebaseGetStorageDataCallback) {
        productList.clear()
        var boardCount = 0 // 지금까지 가져온 게시글 개수를 파악하기 위한 변수!!
        firebaseStoreInstance.collection("Boards").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.getResult()) { // 게시글 개수만큼 반복
                    val item =
                        document.toObject(ProductItem::class.java) // 가져온 Document를 ProductItem으로 캐스팅
                    firebaseDatabaseinstance
                        .getReference("products")
                        .child("img_path")
                        .child(document.id)
                        .get()
                        .addOnSuccessListener {
                            var urlMap: HashMap<String, String>?
                            if (it.getValue() == null) { // 가져온 이미지가 없으면, 또는 이미지가 저장된게 없으면
                                urlMap = HashMap() // Empty hashmap 생성
                            }else { // 이미지가 한개라도 있으면
                                urlMap = it.getValue() as HashMap<String, String> // DataSnapshot에서 이미지 Url들을 HashMap 형태로 캐스팅해서 가져옴
                            }

                            boardCount += 1 // position 1 증가시키기
                            urlMap?.values?.forEach { url ->
                                Log.d("My Uri" + boardCount, url)
                            }
                            item.imgPath = urlMap!!
                            productList.add(item)

                            // 이미지를 모두 가져왔다면 callback 함수로 list 전달
                            if (boardCount == task.getResult().size()) {
                                callback.complete(productList)
                                Log.d("FirebaseRepository", "Complete : ${productList.size}")
                            }
                        }
                }

            }
        }
    }


}