package com.app.buna.sharingmarket.callbacks

import com.app.buna.sharingmarket.model.items.ProductItem

interface IFirebaseGetStoreDataCallback {
    fun complete(data: ArrayList<ProductItem>)
}