package com.yj.widget.recycler;


import com.yj.widget.WidgetActivity;

public class RecyclerViewAdapter<T> extends BaseRecyclerViewAdapter<T> {

    private BaseRecyclerWidget<T> baseListWidget;

    public RecyclerViewAdapter(WidgetActivity activity, BaseRecyclerWidget<T> baseListWidget) {
        super(activity);
        this.baseListWidget = baseListWidget;
    }

    @Override
    protected RecyclerViewHolderWidget<T> onCreateViewHolderWidget(int viewType) {
        return baseListWidget.onCreateViewHolderWidget(viewType);
    }

    @Override
    public T getItem(int position) {
        return baseListWidget.getItem(position);
    }

    @Override
    public int getItemCount() {
        return baseListWidget.getItemCount();
    }


    @Override
    public int getItemViewType(int position) {
        return baseListWidget.getItemViewType(position);
    }

    public int getDataCount() {
        return data == null ? 0 : data.size();
    }

    public T getDataItem(int position) {
        if (position < 0 && position >= data.size()) {
            return null;
        }
        return data.get(position);

    }

}
