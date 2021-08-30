package com.app.buna.sharingmarket.callbacks

interface FirebaseRepositoryCallback {

    fun callbackForSuccessfulUploading(boardUid: String)
    fun callbackForFailureUploading()

}