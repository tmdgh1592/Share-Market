package com.app.buna.sharingmarket.model.items

data class UserModel(
    val board_uid: HashMap<String, String> = HashMap(),
    val jibun: String = "",
    val nickname: String = "",
    val profile_url: String = "",
    val sosock: String = ""
)
