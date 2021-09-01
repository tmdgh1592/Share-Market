package com.app.buna.sharingmarket.callbacks

import com.app.buna.sharingmarket.model.items.ProductItem

interface IFirebaseGetStorageDataCallback {
    fun complete(data: ArrayList<ProductItem>)
}