package com.app.buna.sharingmarket.listners

interface ViewModelListner {
    fun onSuccess(data: Any? = null)
    fun onFail(failType: Int)
}

class FailType{
    companion object {
        const val INTERNET_STATE_ERROR = 1000
        const val NO_DATA = 1001
        const val NO_SELECTED = 1002
    }
}