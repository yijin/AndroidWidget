package com.yj.widget.recycler;

import androidx.recyclerview.widget.RecyclerView;


import com.yj.widget.Widget;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerViewHolderWidget<T> extends Widget {
    private BaseRecyclerViewAdapter mAdapter;
    protected RecyclerView.ViewHolder holder;



    public void setViewHolder(RecyclerView.ViewHolder holder) {
        this.holder = holder;
    }

    public void setAdapter(BaseRecyclerViewAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    protected int getItemCount() {
        return mAdapter.getItemCount();
    }

    protected T getItem(int index) {
        return (T) mAdapter.getItem(index);
    }


    protected final boolean isSelected(int index) {
        return mAdapter.isSelected(index);
    }

    protected final void select(int index) {
        mAdapter.select(index);
    }

    protected final void notifyItemChanged(int position) {
        mAdapter.notifyItemChanged(position);
    }

    protected final void toggleSelect(int index) {
        mAdapter.toggleSelect(index);
    }

    public void selectItem(T object) {
        mAdapter.selectItem(object);
    }

    public void toggleSelectItem(T object) {
        mAdapter.toggleSelectItem(object);
    }

    public boolean isSelectedItem(T object) {
        return mAdapter.isSelectedItem(object);
    }

    public ArrayList<T> getSelectItemList() {
        return mAdapter.getSelectItemList();
    }

    public boolean isEnableSelect() {
        return mAdapter.isEnableSelect();
    }



    public abstract void bindData(int position, T data);

    public void onAttachedToWindow() {

    }

    public void onDetachedFromWindow() {

    }

    public List<T> getAdapterData() {
        return mAdapter.getData();
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public void updateItem(T item) {
        mAdapter.updateItem(item);
    }

    public void updateItem(int index, T item) {
        mAdapter.updateItem(index, item);
    }

    public void remove(T item) {
        mAdapter.remove(item);
    }

    public void remove(int index) {
        mAdapter.remove(index);
    }


}
