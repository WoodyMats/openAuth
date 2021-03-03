package com.woodymats.openauth.interfaces

interface LoginResultCallBack {
    fun onSuccess(message: Int)
    fun onError(message: Int)
}