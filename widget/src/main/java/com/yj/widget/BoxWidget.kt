package com.yj.widget

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * @CreateTime : 2022/6/18 9:33 下午
 * @Author : yijin.yi
 * @Description :
 */
class BoxWidget : GroupWidget() {
    init {
        modifier().wrapContent()
    }

    override fun onCreateView(container: ViewGroup?): View {
        return FrameLayout(activity)
    }
}