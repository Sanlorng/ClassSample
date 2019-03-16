package com.sanlorng.classsample.mvp.image

import android.provider.MediaStore
import com.sanlorng.classsample.fragment.ListShowFragment
import com.sanlorng.classsample.model.Image
import com.sanlorng.classsample.mvp.base.BaseListView
import com.sanlorng.classsample.mvp.base.BasePresenterImpl
import com.sanlorng.classsample.mvp.base.BaseView
import java.text.DateFormat
import java.util.*

class ImageLoadImpl(mView: BaseListView<Image>):BasePresenterImpl<String,ArrayList<Image>,BaseListView<Image>>(mView) {
    override fun afterRequestSuccess(view: BaseListView<Image>?, data: ArrayList<Image>?) {
        view?.run {
            data?.apply {
                when {
                    isEmpty() -> onListLoadNoting()
                    else -> onListLoadFinish(this)
                }
            }
        }
    }

    override fun backgroundRequest(request: String): ArrayList<Image>? {
        return ArrayList<Image>().apply {
            mView?.getViewContext()?.contentResolver?.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN),
                null,
                null,
                "${MediaStore.Images.Media.DATE_TAKEN} desc"
            )?.run {
                while (moveToNext())
                    add(
                        Image(
                        path = getString(getColumnIndex(MediaStore.Images.Media.DATA)),
                            date = DateFormat.getDateInstance()
                                .format(Date(getString(getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)).toLong()))
                    ))
                close()
            }
        }
    }
}

