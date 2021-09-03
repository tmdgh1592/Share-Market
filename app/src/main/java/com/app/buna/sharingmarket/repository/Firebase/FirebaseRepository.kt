package com.app.buna.sharingmarket.repository.Firebase

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import com.app.buna.sharingmarket.callbacks.IFirebaseGetStoreDataCallback
import com.app.buna.sharingmarket.callbacks.IFirebaseRepositoryCallback
import com.app.buna.sharingmarket.model.items.ProductItem
import com.app.buna.sharingmarket.model.items.chat.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class FirebaseRepository {

    // Firebase Realtime DB instance 생성
    val firebaseDatabaseInstance = FirebaseDatabase.getInstance()

    // Firebase Store instacne 생성
    val firebaseStoreInstance = FirebaseFirestore.getInstance()

    // Firebase Storage instacne 생성
    val firebaseStorage = FirebaseStorage.getInstance()
    val productList = ArrayList<ProductItem>()

    companion object {
        // FirebaseDB 싱글톤 생성
        val instance = FirebaseRepository()
    }

    // Realtime DB에 유저 정보 데이터를 저장하는 메소드
    fun saveUserInfo(key: String, data: String, update: Boolean) {
        // realtimeDB
        // 유저 정보 Reference
        if (update) { // 값을 갱신해야 하는 경우 (update == ture)
            firebaseDatabaseInstance.getReference("users") // /user_info
                .child(FirebaseAuth.getInstance().currentUser!!.uid) // /uid
                .child(key) //
                .setValue(data)
        } else { // 값을 누적해야 하는 경우 (update == ture)
            firebaseDatabaseInstance.getReference("users") // /user_info
                .child(FirebaseAuth.getInstance().currentUser!!.uid) // /uid
                .child(key) //
                .push()
                .setValue(data)

        }
    }

    // 탈퇴 사유를 Realtime DB에 저장
    fun saveUnregisterCause(cause: String, id: Int, callback: () -> Unit) {
        // 탈퇴한 유저 Uid 함께 저장
        // 파이어베이스는 비동기이기 때문에 'uid값'이 null값이 될 수도 있으므로 callback사용
        if (id in 1..4) {
            firebaseDatabaseInstance.getReference("unregister").child(cause).push()
                .setValue(Firebase.auth.uid).addOnCompleteListener {
                    callback()
                }
        } else { // 기타 사유인 경우 사유를 따로 보관하기 위해 'other'이라는 경로에 따로 추가
            firebaseDatabaseInstance.getReference("unregister").child("other").child(cause).push()
                .setValue(Firebase.auth.uid).addOnCompleteListener {
                    callback()
                }
        }
    }

    // Realtime DB에 상품 정보에 대한 데이터를 저장하는 메소드
    @SuppressLint("LongLogTag")
    fun saveImgPath(key: String, uri: Uri) {
        // realtimeDB
        // 상품 정보 Reference
        firebaseDatabaseInstance.getReference("products") // product 정보
            .child("img_path")
            .child(key) // 게시글 Uid
            .push()
            .setValue(uri.toString()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(
                        "FirebaseRepository -> saveImgPath",
                        "Successful Save ImgPath in Realtime Database 'img_path' : ${uri}"
                    )
                }
            }
    }

    fun saveProfile(imgUri: Uri, callback: () -> Unit) { // imgUri는 디바이스 이미지 주소
        val profileStorage = firebaseStorage
            .getReferenceFromUrl("gs://sharing-market.appspot.com")
            .child("profiles")
            .child(Firebase.auth.uid.toString())

        profileStorage.putFile(imgUri).addOnSuccessListener {
            // 파이어베이스 DB에도 저장
            profileStorage.downloadUrl.addOnSuccessListener { task ->
                firebaseDatabaseInstance.getReference("users")
                    .child(Firebase.auth.uid.toString()) // uid
                    .child("profile_url")
                    .setValue(task.toString())
                callback()
            }
        }
    }


// DB에 저장한 프로필 Url 가져오는 경우
fun getProfile(uid: String, callback: (String?) -> Unit) {
    firebaseDatabaseInstance.getReference("users")
        .child(uid)
        .child("profile_url")
        .addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val url = snapshot.getValue(String::class.java)
                    callback(url)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("FirebaseRepository", "Canceled")
                }
            }
        )

}


// Storage에서 Profile Uri주소 가져오는 경우
/*fun getProfile(uid: String, callback: (Uri) -> Unit){
    val profileStorage = firebaseStorage.getReferenceFromUrl("gs://sharing-market.appspot.com").child("profiles").child(uid)
    profileStorage.downloadUrl.addOnCompleteListener { task ->
        if(task.isSuccessful) {
            callback(task.result)
        }
    }
}*/


// 파이어베이스 DB는 비동기로 실행되기 때문에 infoData 변수가 초기화되지 않을 수도 있음
// 이 부분 수정해줘야 함
fun getUserInfo(uid: String, key: String): String? {

    var infoData: String? = null

    firebaseDatabaseInstance.getReference("users").child(uid).child(key)
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

// 파이어 스토어에 제품 게시글 '등록'
fun uploadBoard(item: ProductItem, callback: IFirebaseRepositoryCallback) {
    firebaseStoreInstance.collection("Boards")
        .add(item)
        .addOnSuccessListener {
            saveUserInfo("board_uid", it.id, false) // 성공시 realtimeDB의 유저 데이터 목록에 해당 게시글 id 저장
            callback.callbackForSuccessfulUploading(it.id) // MainHomeFragment -> 성공 callback
        }.addOnFailureListener {
            callback.callbackForFailureUploading() // MainHomeFragment -> 실패 callback
        }
}

// 파이어 스토어에 제품 게시글 '수정'
fun updateBoard(item: ProductItem, callback: IFirebaseRepositoryCallback) {
    firebaseStoreInstance.collection("Boards").document(item.documentId)
        .update(
            mapOf(
                "category" to item.category,
                "title" to item.title,
                "content" to item.content,
                "fileNamesForDelete" to item.fileNamesForDelete,
                "give" to item.isGive
            )
        )
        .addOnSuccessListener {
            callback.callbackForSuccessfulUploading(item.documentId)
            Log.d("FirebaseRepository", "Successful for updating board data")
        }.addOnFailureListener {
            callback.callbackForFailureUploading()
        }
}

// Firebase Storage에 이미지 저장
fun saveProductImg(
    imgPath: ArrayList<String>, // local path (디바이스 경로)
    boardUid: String,
    fileNameForDelete: ArrayList<String>
) { // boardUid는 FireStore의 랜덤 push값
    var num = 0
    // 주의 사항 :: 맨 끝에 있는 child는 파일 명임
    imgPath.forEach { path ->
        val storageReference =
            firebaseStorage.getReferenceFromUrl("gs://sharing-market.appspot.com")
                .child("product_images").child("${boardUid}")
                .child(fileNameForDelete.get(num++)) // 파이어 스토리지의 상품 이미지 경로 ex) -> '.../{boardUid}/0.jpeg' 와 같은 형태로 저장됨}

        val uri: Uri = Uri.fromFile(File(path)) // 디바이스 경로를 uri로 변경
        Log.d("FirebaseRepository", "local path : ${uri}") // 기기에 저장되어 있는 이미지 파일 경로

        storageReference.putFile(uri).addOnCompleteListener { task ->
            if (task.isSuccessful) { // Storage에 이미지를 성공적으로 저장했다면
                storageReference.downloadUrl.addOnSuccessListener { resultUri ->
                    saveImgPath(boardUid, resultUri) // 이미지 uri를 RealtimeDB에도 저장 (빠르게 가져오기 위함)
                    Log.d("TEST", resultUri.toString())
                }
            }
        }
    }

}


fun updateProductImg(
    imgPath: HashMap<String, Boolean>, // local path (디바이스 경로) :: key -> path // value -> isLocal
    boardUid: String,
    fileNamesForDelete: ArrayList<String>
) {
    var idx = 0 // fileNamesForDelete를 접근하기 위한 index!!

    if (imgPath.size == 0) { // 업데이트할 때 이미지를 설정하지 않으면 기존 이미지 정보들 제거
        // Delete Storage
        // 잘못삭제되는 경우에 문제가 될 수 있으므로, 이미지를 아예 안올리는 경우나 게시글을 삭제할 때만 Storage삭제
        for (fileName in fileNamesForDelete) {
            Log.d("테스트", fileName + "입니다.")
            firebaseStorage.getReferenceFromUrl("gs://sharing-market.appspot.com")
                .child("product_images").child("${boardUid}").child(fileName)
                .delete()
                .addOnCompleteListener {

                }
        }

        // Delete DB (이미지 경로를 기록한 Scheme)
        firebaseDatabaseInstance.getReference("products") // product 정보
            .child("img_path")
            .child(boardUid) // 게시글 Uid
            .setValue(null)

    } else { // 1개라도 업데이트할 이미지가 있는 경우
        firebaseDatabaseInstance.getReference("products") // product 정보
            .child("img_path")
            .child(boardUid) // 게시글 Uid
            .setValue(null).addOnCompleteListener {
                // 주의 사항 :: 맨 끝에 있는 child는 파일 명임
                imgPath.entries.forEach { entry ->
                    if (idx < imgPath.size) {
                        // 사진을 추가할 storage Reference 경로
                        val storageReference =
                            firebaseStorage.getReferenceFromUrl("gs://sharing-market.appspot.com")
                                .child("product_images").child("${boardUid}")
                                .child(fileNamesForDelete.get(idx++)) // 파이어 스토리지의 상품 이미지 경로 ex) -> '.../{boardUid}/0.jpeg' 와 같은 형태로 저장됨}

                        var uri: Uri
                        Log.d("Is Local", entry.value.toString())
                        if (entry.value) { // 디바이스 경로이면
                            uri = Uri.fromFile(File(entry.key)) // 디바이스 경로를 uri로 변경
                            Log.d("FirebaseRepository", "Local : ${entry.key}")
                        } else { // 기존 FireStorage 주소이면
                            uri = Uri.parse(entry.key) // FireStorage에 있는 이미지 주소를 그대로 Uri로 파싱
                            saveImgPath(boardUid, uri) // 기존 주소 그대로 Firebase Realtime DB에 저장
                            Log.d("FirebaseRepository", "Storage : ${entry.key}")
                        }
                        // 해당 경로에 이미지 저장
                        storageReference.putFile(uri).addOnCompleteListener { task ->
                            if (task.isSuccessful) { // Storage에 이미지를 성공적으로 저장했다면
                                storageReference.downloadUrl.addOnSuccessListener { resultUri ->
                                    saveImgPath(
                                        boardUid,
                                        resultUri
                                    ) // 이미지 uri를 RealtimeDB에도 저장 (빠르게 가져오기 위함)
                                    Log.d("Result", "Success : ${resultUri}")
                                }
                            }
                        }.addOnFailureListener {
                            Log.d("Result", "Fail : ${uri}")
                            Log.d("FirebaseRepository", it.toString())
                        }
                    }
                }
            }
    }


}

// 하트 클릭시 변경
fun clickHeart(
    item: ProductItem,
    nowState: Boolean,
    pushManUid: String,
    callback: (Boolean) -> Unit
) {
    val updateMap = HashMap<String, Any>()

    if (nowState) { // 좋아요 눌려저 있는 경우에 누르면
        item.likeCount = item.likeCount - 1
        item.favorites.keys.remove(pushManUid)
    } else { // 좋아요 안눌러져 있는 경우에 누르면
        item.likeCount = item.likeCount + 1
        item.favorites.put(pushManUid, true)
    }

    updateMap.put("likeCount", item.likeCount)
    updateMap.put("favorites", item.favorites)
    firebaseStoreInstance.collection("Boards")?.document(item.documentId).update(updateMap)
        .addOnCompleteListener {
            callback(!nowState)
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
fun getProductData(category: String, callback: IFirebaseGetStoreDataCallback) {
    productList.clear()
    var boardCount = 0 // 지금까지 가져온 게시글 개수를 파악하기 위한 변수!!

    // FireStore에서 Board 데이터 및 FireDB에서 이미지 경로 가져오는 메소드
    // category 값이 "all"이거나 선택한 category값인 경우에 가져옴
    fun getData(category: String) {
        firebaseStoreInstance.collection("Boards").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) { // 게시글 개수만큼 반복
                    if (category == "all" || document.get("category") == category) {
                        val item =
                            document.toObject(ProductItem::class.java) // 가져온 Document를 ProductItem으로 캐스팅
                        firebaseDatabaseInstance
                            .getReference("products")
                            .child("img_path")
                            .child(document.id)
                            .get()
                            .addOnSuccessListener {
                                var urlMap: HashMap<String, String>? // 이미지 url을 가져올 해시맵
                                if (it.getValue() == null) { // 가져온 이미지가 없으면, 또는 이미지가 저장된게 없으면
                                    urlMap = HashMap() // Empty hashmap 생성
                                } else { // 이미지가 한개라도 있으면
                                    urlMap =
                                        it.getValue() as HashMap<String, String> // DataSnapshot에서 이미지 Url들을 HashMap 형태로 캐스팅해서 가져옴
                                }

                                item.imgPath = urlMap!!
                                item.documentId = document.id // Document Id는 따로 받아옴

                                productList.add(item)

                                // 이미지를 모두 가져왔다면 callback 함수로 list 전달
                                if (boardCount == task.result.size()) {
                                    callback.complete(productList)
                                }
                            }
                    }
                    boardCount += 1 // position 1 증가시키기
                }
            }
        }
    }
    // category를 바탕으로 데이터를 가져옴
    getData(category)
}

// 내가 쓴 게시글 목록들 가져오는 함수
fun getProductData(callback: IFirebaseGetStoreDataCallback) {
    productList.clear()

    // FireStore에서 Board 데이터 및 FireDB에서 이미지 경로 가져오는 메소드
    // category 값이 "all"이거나 선택한 category값인 경우에 가져옴
    fun getData() {
        var boardCount = 0 // 지금까지 가져온 게시글 개수를 파악하기 위한 변수!!
        val uid = Firebase.auth.uid

        firebaseStoreInstance.collection("Boards").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) { // 게시글 개수만큼 반복
                    if (document.get("uid") == uid) {
                        val item =
                            document.toObject(ProductItem::class.java) // 가져온 Document를 ProductItem으로 캐스팅
                        firebaseDatabaseInstance
                            .getReference("products")
                            .child("img_path")
                            .child(document.id)
                            .get()
                            .addOnSuccessListener {
                                var urlMap: HashMap<String, String>? // 이미지 url을 가져올 해시맵
                                urlMap =
                                    if (it.value == null) { // 가져온 이미지가 없으면, 또는 이미지가 저장된게 없으면
                                        HashMap() // Empty hashmap 생성
                                    } else { // 이미지가 한개라도 있으면
                                        it.value as HashMap<String, String> // DataSnapshot에서 이미지 Url들을 HashMap 형태로 캐스팅해서 가져옴
                                    }

                                item.imgPath = urlMap!!
                                item.documentId = document.id // Document Id는 따로 받아옴

                                productList.add(item)

                                // 이미지를 모두 가져왔다면 callback 함수로 list 전달
                                if (boardCount == task.result.size()) {
                                    callback.complete(productList)
                                }
                            }
                    }
                    boardCount += 1 // position 1 증가시키기
                }
            }
        }
    }

    // uid를 바탕으로 데이터를 가져옴 (해당 사용자가 작성한 글만 가져옴)
    getData()
}


// 내가 좋아요 누른 게시글 목록들 가져오는 함수
fun getLikeProductData(callback: IFirebaseGetStoreDataCallback) {
    productList.clear()

    // FireStore에서 Board 데이터 및 FireDB에서 이미지 경로 가져오는 메소드
    // category 값이 "all"이거나 선택한 category값인 경우에 가져옴
    fun getData() {
        var boardCount = 0 // 지금까지 가져온 게시글 개수를 파악하기 위한 변수!!
        val uid = Firebase.auth.uid

        firebaseStoreInstance.collection("Boards").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) { // 게시글 개수만큼 반복
                    if ((document.get("favorites") as MutableMap<String, String>).containsKey(
                            uid
                        )
                    ) {
                        val item =
                            document.toObject(ProductItem::class.java) // 가져온 Document를 ProductItem으로 캐스팅
                        firebaseDatabaseInstance
                            .getReference("products")
                            .child("img_path")
                            .child(document.id)
                            .get()
                            .addOnSuccessListener {
                                var urlMap: HashMap<String, String>? // 이미지 url을 가져올 해시맵
                                urlMap =
                                    if (it.value == null) { // 가져온 이미지가 없으면, 또는 이미지가 저장된게 없으면
                                        HashMap() // Empty hashmap 생성
                                    } else { // 이미지가 한개라도 있으면
                                        it.value as HashMap<String, String> // DataSnapshot에서 이미지 Url들을 HashMap 형태로 캐스팅해서 가져옴
                                    }

                                item.imgPath = urlMap!!
                                item.documentId = document.id // Document Id는 따로 받아옴

                                productList.add(item)

                                // 이미지를 모두 가져왔다면 callback 함수로 list 전달
                                if (boardCount == task.result.size()) {
                                    callback.complete(productList)
                                }
                            }
                    }
                    boardCount += 1 // position 1 증가시키기
                }
            }
        }
    }

    // uid를 바탕으로 데이터를 가져옴 (해당 사용자가 작성한 글만 가져옴)
    getData()
}


// 게시글 삭제
@SuppressLint("LongLogTag")
fun removeProductData(item: ProductItem, callback: (Boolean) -> Unit) {
    // 우선 FireStore에서 게시글에 대한 정보부터 지움
    firebaseStoreInstance.collection("Boards").document(item.documentId).delete()
        .addOnSuccessListener { // 게시글 지우기에 성공했다면 Realtime Database에 기록된 정보들 제거
            callback(true)
        }
        .addOnFailureListener {
            Log.d(
                "FirebaseRepository -> removeProductData() -> firebaseStoreInstance",
                "Failure"
            )
        }


    // DB에 속한 데이터 제거
    // 'users' -> 'board_uid'의 child를 순회하면서 document id를 찾아서 사용자가 지우려는 게시물 제거
    firebaseDatabaseInstance.getReference("users").child(item.uid)
        .child("board_uid").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (snap in snapshot.children) {
                        if (snap.value == item.documentId) {
                            snap.ref.setValue(null)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )


    // 이미지가 있을땐 DB의 이미지 경로들도 제거
    firebaseDatabaseInstance.getReference("products")
        .child("img_path") // 'products' -> documentId 찾아서 제거
        .child(item.documentId).removeValue().addOnSuccessListener {
            Log.d("", "Remove Successful")
        }.addOnFailureListener {
            Log.d(
                "FirebaseRepository -> removeProductData() -> firebaseDatabaseinstance.products",
                "Failure"
            )
        }

    /*
    * Storage는 Directory를 지우거나, 모든 파일을 지우는 메소드가 없으므로.
    * url 개수만큼 반복하면서 Image file 제거
    * */
    for (fileName in item.fileNamesForDelete) {
        firebaseStorage.getReferenceFromUrl("gs://sharing-market.appspot.com")
            .child("product_images").child("${item.documentId}").child(fileName)
            .delete()
            .addOnCompleteListener {

            }
    }
}

fun shareDone(isDone: Boolean, documentId: String, callback: () -> Unit) {
    val doc = firebaseStoreInstance.collection("Boards").document(documentId)
    firebaseStoreInstance.runTransaction { transaction ->
        val board = transaction.get(doc).toObject(ProductItem::class.java)
        board?.isComplete = isDone
        transaction.set(doc, board!!)
        callback() // 트랜잭션이 끝나면 액티비티를 종료하기 위한 callback을 호출
    }
}

// 채팅을 보내는 메소드
fun sendMessage(
    chatRoomUid: String?,
    users: HashMap<String, Boolean>,
    comment: ChatModel.Comment,
    complete: () -> Unit
) {
    if (chatRoomUid == null) { // 채팅방이 없다면 새로운 채팅방 생성
        // 새로운 채팅 맵 생성
        val commentMap = HashMap<String, ChatModel.Comment>().apply {
            put(comment.uid, comment)
        }
        firebaseDatabaseInstance.reference.child("chatrooms").push()
            .setValue(ChatModel(users, commentMap)).addOnCompleteListener {
                // 메세지 전송 완료시 complete 콜백
                complete()
            }
    } else { // 채팅방이 있다면 기존 채팅방에 채팅기록 추가
        firebaseDatabaseInstance.reference.child("chatrooms").child(chatRoomUid)
            .child("comments").push().setValue(comment).addOnCompleteListener {
                // 메세지 전송 완료시 complete 콜백
                complete()
            }
    }
}

fun getComments(chatRoomUid: String?, complete: (ArrayList<ChatModel.Comment>) -> Unit) {
    if (chatRoomUid != null) {
        firebaseDatabaseInstance.getReference("chatrooms").child(chatRoomUid).child("comments")
            .orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = ArrayList<ChatModel.Comment>() // 채팅 기록 리스트
                    // 채팅방 채팅 내역들을 가져와서 chatList에 추가
                    snapshot.children.forEach { item ->
                        val comment = item.getValue(ChatModel.Comment::class.java)
                        comment?.let { chatList.add(it) }
                    }
                    complete(chatList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("FirebaseRepository", "getComments is Canceled.")
                }
            })
    }
}


// 본인이 들어가 있는 채팅방을 돌면서 상대방이 있는지 확인하고, 있다면 채팅방의 Uid를 가져옴
fun checkChatRoom(destUid: String, callback: (String?) -> Unit) {
    firebaseDatabaseInstance.getReference("chatrooms")
        .orderByChild("users/" + Firebase.auth.uid!!).equalTo(true)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { item ->
                    val chatModel = item.getValue(ChatModel::class.java)
                    if (chatModel?.users?.containsKey(destUid)!!) {
                        Log.d("FirebaseRepository", "checkChatRoom Callback Called")
                        callback(item.key) // 채팅방 Uid 전달
                        return@forEach
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        )
}


}