package com.app.buna.sharingmarket.model.items.chat

import com.app.buna.sharingmarket.CommentType
import com.app.buna.sharingmarket.utils.ChatTimeStampUtil

data class ChatModel(val users: HashMap<String, Boolean> = HashMap(), val comments: HashMap<String, Comment> = HashMap()) {
    data class Comment(val uid: String = "", val message:String = "", val timeStamp: Long = 0L, val commentType: Int = CommentType.COMMENT) {

        var usingTimeStamp: String =ChatTimeStampUtil.getStamp(timeStamp)
        private set
    }
}
