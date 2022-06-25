package com.yj.widget

import android.view.ViewGroup

/**
 * @CreateTime : 2022/6/18 9:15 下午
 * @Author : yijin.yi
 * @Description :
 */
abstract class GroupWidget(val childs: List<Widget>? = null) : Widget() {

    override fun onCreatedView() {
        super.onCreatedView()
        onAddChilds()
    }

    open protected fun onCreateChilds(): List<Widget>? {
        return childs
    }

    open protected fun onAddChilds() {
        childs?.forEach {
            loadChildWidget(contentView as ViewGroup, it)
        }
    }


}