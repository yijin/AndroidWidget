package com.yj.widget


import android.widget.LinearLayout

/**
 * @CreateTime : 2022/6/18 9:02 下午
 * @Author : yijin.yi
 * @Description :
 */
class RowWidget(childs: List<Widget>? = null) : LinearWidget(childs) {
    init {
        modifier().widthMatchParent().heightWrapContent()
    }

    override fun createLinearLayout(): LinearLayout {
        var layout = LinearLayout(activity)
        layout.orientation = LinearLayout.HORIZONTAL
        return layout
    }
}