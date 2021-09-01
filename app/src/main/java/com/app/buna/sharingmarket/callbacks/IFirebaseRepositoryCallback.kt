package com.app.buna.sharingmarket.callbacks

interface IFirebaseRepositoryCallback {

    fun callbackForSuccessfulUploading(boardUid: String)
    fun callbackForFailureUploading()

}