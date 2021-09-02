package com.app.buna.sharingmarket.callbacks

// 파이어 스토어에서 업로드한 Board의 성공 여부와 board Uid를 가져오는 Callback
interface IFirebaseRepositoryCallback {

    fun callbackForSuccessfulUploading(boardUid: String)
    fun callbackForFailureUploading()

}