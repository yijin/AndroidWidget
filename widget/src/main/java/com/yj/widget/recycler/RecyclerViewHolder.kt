package com.yj.widget.recycler

import androidx.recyclerview.widget.RecyclerView

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
class RecyclerViewHolder<T>(val widget: RecyclerViewHolderWidget<T>) :
    RecyclerView.ViewHolder(
        widget.contentView!!
    ) {

   init {

        widget.setViewHolder(this)
    }
}