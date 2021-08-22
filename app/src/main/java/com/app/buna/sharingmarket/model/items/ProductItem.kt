package com.app.buna.sharingmarket.model.items

data class ProductItem(
    val id: String, // 게시글 아이디
    val owner: String,  // 상품 주인
    val name: String,   // 상품명
    val category: String, // 카테고리
    val location: String, // 지역
    val time: String, // 게시글 업로드 시간
    val uri: ArrayList<String>, // 이미지들 uri
    val title: String, // 게시글 제목
    val content: String, // 게시글 내용
    val likeCount: Int, // 좋아요 개수
    val isComplete: Boolean
) {

    val likeCountStr: String
    get() = Integer.toString(likeCount)

    val processedTime: String
    get() = time

    val locationAndTime: String
    get() = location + " · " + processedTime
}