package com.app.buna.sharingmarket.model.items

data class ProductItem(
    val owner: String,  // 상품 주인
    val name: String,   //
    val type: String,
    val location: String,
    val time: String,
    val uri: ArrayList<String>,
    val content: String,
    val likeCount: Int
) {

    val likeCountStr: String
    get() = Integer.toString(likeCount)

    val processedTime: String
    get() = time

    val locationAndTime: String
    get() = location + " · " + processedTime
}