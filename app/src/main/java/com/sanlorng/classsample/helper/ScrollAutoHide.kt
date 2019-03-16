package com.sanlorng.classsample.helper

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat

class ScrollAutoHide(context: Context, attr: AttributeSet) : CoordinatorLayout.Behavior<View>(context, attr) {
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        if (dyConsumed > 0)
            animateOut(child)
        else if (dyConsumed < 0)
            animateIn(child)
    }

    private fun animateOut(target: View) {
        val layoutParams = target.layoutParams as CoordinatorLayout.LayoutParams
        val bottomMargin = layoutParams.bottomMargin
        target.animate().translationY((target.height + bottomMargin).toFloat()).setInterpolator(LinearInterpolator())
            .start()

    }

    private fun animateIn(target: View) {
        target.animate().translationY(0f).setInterpolator(LinearInterpolator()).start()
    }
}