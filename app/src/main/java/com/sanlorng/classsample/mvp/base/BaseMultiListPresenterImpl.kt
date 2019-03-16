package com.sanlorng.classsample.mvp.base

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.core.net.toUri
import com.sanlorng.kit.BuildConfig
import com.sanlorng.kit.defaultSharedPreference
import com.sanlorng.kit.dialog
import com.sanlorng.kit.exceptionDialog
import kotlinx.coroutines.*
import java.io.PrintWriter
import java.io.StringWriter
import java.net.ConnectException
import java.net.SocketTimeoutException

abstract class BaseMultiListPresenterImpl<R,D,V : BaseListView<D>>(var mView: V?) : BasePresenter, PresenterListInter<R,D,V>{
    private var job: Job? = null

    override fun cancelJob() {
        job?.cancel()
    }

    override fun detachView() {
        cancelJob()
        mView = null
    }

    fun doRequest(request: R) {
        cancelJob()
        job = doRequest(mView, request)
    }
}
interface PresenterListInter<R,D,V:BaseListView<D>> {
    fun doRequest(mView:V?,request: R) = mView?.run {
        if (this is BaseNetworkView) {
            run {
                if (!getViewContext().isNetworkActive) {
                    onNetworkFailed()
                    return null
                }
                onRequesting()
                GlobalScope.launch {
                    try {
                        backgroundRequest(request).also {
                            withContext(Dispatchers.Main) {
                                try {
                                    onRequestFinished()
                                    afterRequestSuccess(mView,it)
                                }catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        onRequestFinished()
                                        if (BuildConfig.DEBUG)
                                            e.printStackTrace()
                                        else
                                            onException(getViewContext(),e)
                                    }
                                }catch (e: java.lang.Exception) {
                                    withContext(Dispatchers.Main) {
                                        onRequestFinished()
                                        if (BuildConfig.DEBUG)
                                            e.printStackTrace()
                                        else
                                            onException(getViewContext(),e)
                                    }
                                }
                            }
                        }
                    } catch (e: SocketTimeoutException) {
                        withContext(Dispatchers.Main) {
                            onNetworkFailed()
                            onRequestFinished()
                            if (BuildConfig.DEBUG)
                                e.printStackTrace()
                            else
                                getViewContext().exceptionDialog(e)
                        }
                    } catch (e: ConnectException) {
                        withContext(Dispatchers.Main) {
                            onNetworkFailed()
                            onRequestFinished()
                            if (BuildConfig.DEBUG)
                                e.printStackTrace()
                            else
                                getViewContext().exceptionDialog(e)
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onRequestFinished()
                            if (BuildConfig.DEBUG)
                                e.printStackTrace()
                            else
                                onException(getViewContext(),e)

                        }
                    }catch (e: java.lang.Exception) {
                        withContext(Dispatchers.Main) {
                            onRequestFinished()
                            if (BuildConfig.DEBUG)
                                e.printStackTrace()
                            else
                                onException(getViewContext(),e)
                        }
                    }
                }
            }
            return null
        }
        onRequesting()
        GlobalScope.launch {
            try {
                backgroundRequest(request).apply {
                    withContext(Dispatchers.Main) {
                        try {
                            onRequestFinished()
                            afterRequestSuccess(mView,this@apply)
                        }catch (e: java.lang.Exception) {
                            withContext(Dispatchers.Main) {
                                onRequestFinished()
                                if (BuildConfig.DEBUG)
                                    e.printStackTrace()
                                else
                                    onException(getViewContext(),e)
                            }
                        }catch (e:Exception) {
                            withContext(Dispatchers.Main) {
                                onRequestFinished()
                                if (BuildConfig.DEBUG)
                                    e.printStackTrace()
                                else
                                    onException(getViewContext(),e)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onRequestFinished()
                    if (BuildConfig.DEBUG)
                        e.printStackTrace()
                    else
                        onException(getViewContext(),e)
                }
            }catch (e: java.lang.Exception) {
                withContext(Dispatchers.Main) {
                    onRequestFinished()
                    if (BuildConfig.DEBUG)
                        e.printStackTrace()
                    else
                        onException(getViewContext(),e)
                }
            }
        }
    }
    private fun onException(context: Context, e: Exception){
        context.run {
            if (defaultSharedPreference.getBoolean("show_exception_dialog",true))
                dialog("应用运行时发生错误","如果您愿意，是否能将错误信息发送给我们","发送邮件", DialogInterface.OnClickListener { _, _ ->
                    startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:${defaultSharedPreference.getString("userMail","sanlorng@sample.com")}".toUri()
                        putExtra(Intent.EXTRA_SUBJECT,packageName + "发生了错误")
                        putExtra(Intent.EXTRA_TEXT, StringWriter().apply {
                            e.printStackTrace(PrintWriter(this))
                        }.toString())
                    },"选择客户端发送"))
                })
        }
    }
    fun backgroundRequest(request: R): ArrayList<D>
    fun afterRequestSuccess(view:V?,data: ArrayList<D>)
}