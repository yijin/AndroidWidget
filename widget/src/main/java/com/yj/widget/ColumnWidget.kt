package com.yj.widget

import android.widget.LinearLayout

/**
 * @CreateTime : 2022/6/18 9:02 下午
 * @Author : yijin.yi
 * @Description :
 */
class ColumnWidget(childs: List<Widget>? = null) : LinearWidget(childs) {
    init {
        modifier().heightMatchParent().widthWrapContent()
    }

    override fun createLinearLayout(): LinearLayout {
        var layout = LinearLayout(activity)
        layout.orientation = LinearLayout.VERTICAL
        return layout
    }
}