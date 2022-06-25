package com.yj.widget.recycler;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.market.uikit.adapter.HeaderAndFooterWrapper;
import com.yj.widget.Widget;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerWidget<T> extends Widget implements ScrollableHelper.ScrollableContainer {
    protected RecyclerViewAdapter<T> mAdapter;
    protected RecyclerView recyclerView;
    protected HeaderAndFooterWrapper headerAndFooterWrapper;
    protected RecyclerView.LayoutManager layoutManager;


    @NonNull
    @Override
    protected View onCreateView(@Nullable ViewGroup container) {
        RecyclerView recyclerView = new RecyclerView(getActivity());
        initRecyclerView(recyclerView);
        return recyclerView;
    }

    protected void initRecyclerView(RecyclerView recyclerView) {
        this.mAdapter = createAdapter();
        headerAndFooterWrapper = new HeaderAndFooterWrapper(mAdapter);
        layoutManager = createLayoutManager();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(headerAndFooterWrapper);
        this.recyclerView = recyclerView;
    }

    protected RecyclerViewAdapter<T> createAdapter() {
        return new RecyclerViewAdapter(getActivity(), this);
    }


    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public LinearLayoutManager createVerticalLinearLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        return linearLayoutManager;
    }

    public LinearLayoutManager createHorizontalLinearLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        return linearLayoutManager;
    }


    protected RecyclerView.LayoutManager createLayoutManager() {
        return createVerticalLinearLayoutManager();
    }


    public void addHeaderWidget(Widget widget) {
        loadChildWidget(recyclerView, widget
                .modifier()
                .disableAddView()
                .get());
        headerAndFooterWrapper.addHeaderView(widget.getContentView());
    }

    public void addHeaderWidget(int pos, Widget widget) {
        loadChildWidget(recyclerView, widget);
        headerAndFooterWrapper.addHeaderView(pos, widget.getContentView());
    }


    public void removeHeaderWidget(Widget widget) {
        headerAndFooterWrapper.removeHeaderView(widget.getContentView());
        widget.remove();
    }


    public void addFooterWidget(Widget widget) {
        loadChildWidget(recyclerView, widget.modifier().disableAddView().get());
        headerAndFooterWrapper.addFootView(widget.getContentView());

    }


    public void addFooterWidget(int pos, Widget widget) {
        loadChildWidget(recyclerView, widget.modifier().disableAddView().get());
        headerAndFooterWrapper.addFootView(pos, widget.getContentView());
    }

    public void removeFooterWidget(Widget widget) {
        headerAndFooterWrapper.removeFooterView(widget.getContentView());
        widget.remove();
    }


    public void addData(List<T> list) {
        mAdapter.addData(list);
    }

    public void addData(List<T> list, boolean front) {
        mAdapter.addData(list, front);
    }

    public void setData(List<T> data) {
        if (mAdapter != null) {
            mAdapter.setData(data);
        }
    }

    public void bindData(List<T> data) {
        mAdapter.bindData(data);
    }


    public void addItem(T item) {
        mAdapter.addItem(item);
    }

    public void addItem(int index, T item) {
        mAdapter.addItem(index, item);
    }


    public boolean remove(T item) {
        return mAdapter.remove(item);
    }

    public T remove(int index) {
        return mAdapter.remove(index);
    }

    public void updateItem(int index, T item) {
        mAdapter.updateItem(index, item);
    }

    public void updateItem(T item) {
        mAdapter.updateItem(item);
    }

    public List<T> getData() {
        return mAdapter.getData();
    }


    public T getItem(int position) {
        return mAdapter.getDataItem(position);
    }


    public int getItemCount() {
        if (mAdapter == null) {
            return 0;
        }
        return mAdapter.getDataCount();
    }


    public int getItemViewType(int position) {
        return 0;
    }

    public int getSelectIndex() {
        return mAdapter.getSelectIndex();
    }

    public T getSelectItem() {
        return mAdapter.getSelectItem();
    }

    public void selectItem(T object) {
        mAdapter.selectItem(object);
    }

    public List<String> getSelectIndexList() {
        return mAdapter.getSelectIndexList();
    }

    public ArrayList<T> getSelectItemList() {
        return mAdapter.getSelectItemList();
    }

    public void addSelectItemList(ArrayList<T> selectItemList) {
        mAdapter.addSelectItemList(selectItemList);
    }


    public void select(int index) {
        mAdapter.select(index);
    }

    public void toggleSelect(int index) {
        mAdapter.toggleSelect(index);
    }

    public void enableMultipleSelect() {
        mAdapter.enableMultipleSelect();
    }

    public boolean isEnableSelect() {
        return mAdapter.isEnableSelect();
    }

    public void setEnableSelect(boolean enableSelect) {
        this.mAdapter.setEnableSelect(enableSelect);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public void selectObject(T object) {
        mAdapter.selectItem(object);
    }

    public void toggleSelectObject(T object) {
        mAdapter.toggleSelectItem(object);
    }

    public boolean isSelectedObject(T object) {
        return mAdapter.isSelectedItem(object);
    }

    public void clearSelected() {
        mAdapter.clearSelected();
    }


    public abstract RecyclerViewHolderWidget<T> onCreateViewHolderWidget(int viewType);


    public void smoothScrollToPosition(int position) {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(position);
        }

    }

    public void scrollToPosition(int position) {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(position);
        }

    }

    public void scrollToEnd() {
        if (getItemCount() > 0) {
            scrollToPosition(getItemCount() - 1);
        }
    }

    public void notifyItemRangeInserted(int positionStart, int itemCount) {
        if (mAdapter != null) {
            mAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }
    }

    public void notifyItemChanged(int position) {
        if (mAdapter != null) {
            mAdapter.notifyItemChanged(position);
        }
    }

    public void notifyItemRemoved(int position) {
        if (mAdapter != null) {
            mAdapter.notifyItemRemoved(position);
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }


}
