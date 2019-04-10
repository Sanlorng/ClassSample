package com.sanlorng.classsample.helper

import android.view.View
import android.view.ViewGroup
import androidx.core.view.*

object App {
}

fun View.marginTopStatusBarHeight() {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        (layoutParams as ViewGroup.MarginLayoutParams).apply {
            topMargin += context.let {
                it.resources.getDimensionPixelOffset(it.resources.getIdentifier("status_bar_height","dimen","android"))
            }
            layoutParams = this
        }
    }
}

fun View.paddingTopStatusBarHeight() {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        (layoutParams as ViewGroup.MarginLayoutParams).apply {
            val padding = paddingTop + context.let {
                it.resources.getDimensionPixelOffset(it.resources.getIdentifier("status_bar_height","dimen","android"))
            }
            setPadding(paddingLeft,padding,paddingRight,paddingBottom)
            layoutParams = this
        }
    }
}

fun View.adjustMargin(percent: Float) {
    when(layoutParams) {
        is ViewGroup.MarginLayoutParams -> {
            (layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins((marginLeft*percent).toInt(),
                    (marginTop*percent).toInt(),
                    (marginRight*percent).toInt(),
                    (marginBottom*percent).toInt())
                setPadding((paddingLeft*percent).toInt(),
                    (paddingTop*percent).toInt(),
                    (paddingRight*percent).toInt(),
                    (paddingBottom*percent).toInt())
                if (height > 0)
                    height = (height * percent).toInt()
                if (width > 0)
                    width = (width * percent).toInt()
            }
        }
    }
}
