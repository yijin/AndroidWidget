package com.yj.widget.page


import androidx.annotation.AnimRes
import androidx.lifecycle.Lifecycle
import com.yj.widget.*
import com.yj.widget.page.PageWidgetManager.Companion.START_NORMAL

class PageWidgetStartBuilder internal constructor(
    val widgetManager: WidgetManager,
    val page: PageWidgetWrapper,
    val pageWidgetManager: PageWidgetManager,
    val startType: Int = START_NORMAL

) {

    val params: PageWidgetCreateParams =
        PageWidgetCreateParams(widgetManager, startType)


    fun start(@AnimRes enterAnimResId: Int, @AnimRes exitAnimResId: Int): Boolean {
        if (
            widgetManager.activity.lifecycle.currentState == Lifecycle.State.DESTROYED
        ) {
            return false
        }

        params.enterAnimResId = enterAnimResId
        page.exitAnimResId = exitAnimResId
        pageWidgetManager.start(page, params)
        return true

    }


    fun start(): Boolean {
        return startRightToLeft()
    }

    fun startNoAnim(): Boolean {
        return start(0, 0)
    }


    fun startBottomToTop(): Boolean {
        return start(
            R.anim.widget_bottom_to_top_enter,
            R.anim.widget_bottom_to_top_exit
        )
    }

    fun startTopToBottom(): Boolean {
        return start(
            R.anim.widget_top_to_bottom_enter,
            R.anim.widget_top_to_bottom_exit
        )
    }

    fun startLeftToRight(): Boolean {
        return start(
            R.anim.widget_left_to_right_enter,
            R.anim.widget_left_to_right_exit
        )
    }

    fun startRightToLeft(): Boolean {
        return start(
            R.anim.widget_right_to_left_enter,
            R.anim.widget_right_to_left_exit
        )
    }


}
