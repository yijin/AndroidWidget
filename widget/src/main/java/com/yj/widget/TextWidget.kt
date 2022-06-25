package com.yj.widget

import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * @CreateTime : 2022/6/18 9:33 下午
 * @Author : yijin.yi
 * @Description :
 */
class TextWidget : GroupWidget() {
    init {
        modifier().wrapContent()
    }

    protected var textView: TextView? = null

    override fun onCreateView(container: ViewGroup?): View {
        textView = TextView(activity)
        return textView!!
    }



}