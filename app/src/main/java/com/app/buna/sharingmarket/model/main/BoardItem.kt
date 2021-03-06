package com.app.buna.sharingmarket.model.main

import android.os.Parcelable
import com.app.buna.sharingmarket.utils.TimeUtil
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BoardItem(
    var uid: String = "null", // 게시자 uid
    var owner: String = "null",  // 상품 주인
    var category: String = "null", // 카테고리
    var location: String = "null", // 지역
    var time: Long = 0L, // 게시글 업로드 시간
    var title: String = "null", // 게시글 제목
    var content: String = "null", // 게시글 내용
    var likeCount: Int = 0, // 좋아요 개수
    var isComplete: Boolean = false, // 거래 완료된지 여부
    var isGive: Boolean = false, // 주는건지, 필요한건지
    var isExchange: Boolean = false, // 교환인지, 아닌지
    var favorites: MutableMap<String, Boolean> = HashMap(), // 좋아요 누른 사람 목록
    // fileNamesForDelete => Storage에 저장될 이미지 파일 이름 (중복되지 않는 값으로 저장해야함, 최대 길이 13), 이미지 파일 삭제할 때 고유 값으로 사용할 용도!!
    var fileNamesForDelete: ArrayList<String> = ArrayList(),
    @Exclude
    var documentId: String = "null", // 게시글 uid
    @Exclude
    var imgPath: HashMap<String, String> = HashMap() // FireStorage에 저장된 이미지 경로
): Parcelable {


    @Exclude
    fun getLocationAndTime(): String {
        return (location + " · " + getTimeAgo())
    }

    @Exclude
    fun getTimeAgo(): String {
        return TimeUtil.getTimeAgo(time) // ~초 전, ~분 전 구하는 Util
    }

    @Exclude
    fun getCategoryAndTime(): String {
        return (category + " · " + getTimeAgo())
    }
}