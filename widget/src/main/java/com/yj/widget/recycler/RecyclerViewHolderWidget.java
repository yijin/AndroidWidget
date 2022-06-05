package com.yj.widget.recycler;

import androidx.recyclerview.widget.RecyclerView;

import com.yj.widget.Widget;


public abstract class RecyclerViewHolderWidget<T> extends Widget {
    private RecyclerViewAdapter mAdapter;
    protected RecyclerView.ViewHolder holder;


    public void setViewHolder(RecyclerView.ViewHolder holder) {
        this.holder = holder;
    }

    public void setAdapter(RecyclerViewAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    protected int getItemCount() {
        return mAdapter.getItemCount();
    }


    protected final void notifyItemChanged(int position) {
        mAdapter.notifyItemChanged(position);
    }


    protected abstract int getLayoutId();

    public abstract void bindData(int position, T data);

    public void onAttachedToWindow() {

    }

    public void onDetachedFromWindow() {

    }


    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }


}
