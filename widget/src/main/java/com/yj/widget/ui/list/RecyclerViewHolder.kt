package com.yj.widget.ui.list

import androidx.recyclerview.widget.RecyclerView

class RecyclerViewHolder<T>(val widget: RecyclerViewHolderWidget<T>) :
    RecyclerView.ViewHolder(
        widget.contentView!!
    ) {

   init {

        widget.setViewHolder(this)
    }
}