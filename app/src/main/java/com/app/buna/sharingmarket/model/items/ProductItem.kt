package com.app.buna.sharingmarket.model.items

data class ProductItem(
    val owner: String,  // 상품 주인
    val category: String, // 카테고리
    val location: String, // 지역
    val time: String, // 게시글 업로드 시간
    val uri: ArrayList<String>, // 이미지들 uri
    val title: String, // 게시글 제목
    val content: String, // 게시글 내용
    val likeCount: Int, // 좋아요 개수
    val isComplete: Boolean, // 거래 완료된지 여부
    val isGive: Boolean // 주는건지, 필요한건지
) {


    fun getLocationAndTime(): String {
        return (location + " · " + getTimeAgo())
    }

    fun getTimeAgo(): String{
        return "1일 전"
    }
}