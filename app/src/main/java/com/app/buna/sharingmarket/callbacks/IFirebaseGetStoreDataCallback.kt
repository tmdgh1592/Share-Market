package com.app.buna.sharingmarket.callbacks

import com.app.buna.sharingmarket.model.main.BoardItem

interface IFirebaseGetStoreDataCallback {
    fun complete(data: ArrayList<BoardItem>)
}