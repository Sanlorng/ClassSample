package com.sanlorng.classsample.helper

import android.view.View
import android.view.ViewGroup
import androidx.core.view.*

object App {
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
