package com.yj.widget

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

/**
 * @CreateTime : 2022/6/18 9:03 下午
 * @Author : yijin.yi
 * @Description :
 */
abstract class LinearWidget(childs: List<Widget>? = null) : GroupWidget(childs) {

    init {
        modifier().wrapContent()
    }


    private var linearLayout: LinearLayout? = null

    override fun onCreateView(container: ViewGroup?): View {
        linearLayout = createLinearLayout()
        return linearLayout!!
    }

    abstract fun createLinearLayout(): LinearLayout

    fun setVerticalGravity(verticalGravity: Int) {
        linearLayout?.setVerticalGravity(verticalGravity)
    }

    fun setHorizontalGravity(horizontalGravity: Int) {
        linearLayout?.setHorizontalGravity(horizontalGravity)
    }

    fun setWeightSum(weightSum: Float) {
        linearLayout?.setWeightSum(weightSum)
    }

}