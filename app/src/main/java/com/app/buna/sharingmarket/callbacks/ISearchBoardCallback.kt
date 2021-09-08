package com.app.buna.sharingmarket.callbacks

interface ISearchBoardCallback {
    fun callback(keyword: String)
    fun cancel()
}