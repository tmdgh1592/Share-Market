package com.app.buna.sharingmarket.callbacks

import com.app.buna.sharingmarket.model.items.BoardItem

interface IFirebaseGetStoreDataCallback {
    fun complete(data: ArrayList<BoardItem>)
}