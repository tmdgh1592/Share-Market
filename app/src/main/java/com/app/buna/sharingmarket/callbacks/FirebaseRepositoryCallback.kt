package com.app.buna.sharingmarket.callbacks

interface FirebaseRepositoryCallback {

    fun callbackForSuccessfulUploading(uid: String)
    fun callbackForFailureUploading()

}