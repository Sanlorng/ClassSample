package com.sanlorng.classsample.mvp.base

import android.content.Context

interface BasePresenter {
    fun detachView()
    fun cancelJob()
}
interface BaseListView<D>:BaseView {
    fun onListLoadFinish(result:ArrayList<D>)
    fun onListLoadNoting(){}
    fun onListLoadFailed(){}
}
interface BaseView {
    fun getViewContext(): Context
    fun onRequesting(){}
    fun onRequestFinished(){}
}

interface BaseNetworkView:BaseView {
    fun onNetworkFailed()
}

val Context.isNetworkActive
get() = true
