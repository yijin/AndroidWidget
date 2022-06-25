package com.yj.widget.recycler;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseRecyclerViewHolder<T> extends RecyclerView.ViewHolder {


    private RecyclerViewHolderWidget<T> widget;

    protected BaseRecyclerViewHolder(@NonNull RecyclerViewHolderWidget widget) {
        super(widget.getContentView());
        this.widget = widget;
        widget.setViewHolder(this);
    }

    public RecyclerViewHolderWidget<T> getWidget() {
        return widget;
    }
}
