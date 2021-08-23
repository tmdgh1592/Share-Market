package com.app.buna.sharingmarket.model.items

import android.net.Uri
import com.app.buna.sharingmarket.utils.TimeUtil
import com.google.firebase.firestore.Exclude

data class ProductItem(
    val owner: String = "null",  // 상품 주인
    val category: String = "null", // 카테고리
    val location: String = "null", // 지역
    val time: Long = 0L, // 게시글 업로드 시간
    val title: String = "null", // 게시글 제목
    val content: String = "null", // 게시글 내용
    val likeCount: Int = 0, // 좋아요 개수
    val isComplete: Boolean = false, // 거래 완료된지 여부
    val isGive: Boolean = false, // 주는건지, 필요한건지
    val isExchange: Boolean = false, // 교환인지, 아닌지
    var imgPath: ArrayList<Uri> = ArrayList() // FireStorage에 저장된 이미지 경로
) {



    @Exclude
    fun getLocationAndTime(): String {
        return (location + " · " + getTimeAgo())
    }

    @Exclude
    fun getTimeAgo(): String {
        return TimeUtil.getTimeAgo(time) // ~초 전, ~분 전 구하는 Util
    }
}