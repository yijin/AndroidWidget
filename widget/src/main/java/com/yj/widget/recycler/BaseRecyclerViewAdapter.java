package com.yj.widget.recycler;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.yj.widget.WidgetActivity;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter {


    protected RecyclerWidget recyclerWidget;

    public BaseRecyclerViewAdapter(RecyclerWidget recyclerWidget) {
        this.recyclerWidget = recyclerWidget;
    }

    private int selectIndex = -1;
    private T selectItem = null;
    private List<String> selectIndexList = new ArrayList<>();

    private ArrayList<T> selectItemList = new ArrayList<>();


    private boolean enableMultipleSelect = false;
    private boolean enableSelect = true;

    public boolean isEnableSelect() {
        return enableSelect;
    }

    public void setEnableSelect(boolean enableSelect) {
        this.enableSelect = enableSelect;
    }

    public void enableMultipleSelect() {
        enableMultipleSelect = true;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public T getSelectItem() {
        return selectItem;
    }

    public List<String> getSelectIndexList() {
        return selectIndexList;
    }

    public ArrayList<T> getSelectItemList() {
        if (selectItemList.size() == 0 && selectIndexList.size() > 0) {
            ArrayList<T> itemList = new ArrayList();
            for (String str : selectIndexList
            ) {
                int index = Integer.parseInt(str);
                itemList.add(getItem(index));

            }
            return itemList;
        }
        return selectItemList;
    }

    public void addSelectItemList(ArrayList<T> list) {
        this.selectItemList.addAll(list);
    }

    public void clearSelected() {
        selectIndex = -1;
        selectIndexList.clear();
        selectItem = null;
        selectItemList.clear();
        notifyDataSetChanged();
    }

    public void select(int index) {
        if (enableMultipleSelect) {
            if (selectIndexList.contains(index + "")) {
                return;
            }
            selectIndexList.add(index + "");

        } else {
            if (index == this.selectIndex) {
                return;
            }
            int oldSelectIndex = this.selectIndex;
            this.selectIndex = index;
            selectItem = getItem(index);
            if (oldSelectIndex >= 0) {
                notifyItemChanged(oldSelectIndex);
            }
            if (index >= 0) {
                notifyItemChanged(index);
            }
        }

    }

    public void selectItem(T object) {
        if (enableMultipleSelect) {
            if (selectItemList.contains(object)) {
                return;
            }
            selectItemList.add(object);

        } else {
            int oldSelectIndex = this.selectIndex;
            this.selectItem = object;
            this.selectIndex = data.indexOf(object);
            if (oldSelectIndex >= 0) {
                notifyItemChanged(oldSelectIndex);
            }
            if (this.selectIndex >= 0) {
                notifyItemChanged(this.selectIndex);
            }
        }

    }

    public void toggleSelect(int index) {

        if (enableMultipleSelect) {
            if (selectIndexList.contains(index + "")) {
                selectIndexList.remove(index + "");
                notifyItemChanged(index);
            } else {
                selectIndexList.add(index + "");
            }
            notifyItemChanged(index);

        } else {
            if (index == this.selectIndex) {
                this.selectIndex = -1;
                selectItem = null;
                notifyItemChanged(index);
                return;
            }
            int oldSelectIndex = this.selectIndex;
            this.selectIndex = index;
            selectItem = getItem(index);
            if (oldSelectIndex >= 0) {
                notifyItemChanged(oldSelectIndex);
            }
            notifyItemChanged(index);
        }

    }

    public void toggleSelectItem(T object) {

        if (enableMultipleSelect) {
            if (selectItemList.contains(object)) {
                selectItemList.remove(object);

            } else {
                selectItemList.add(object);
            }
            int index = data.indexOf(object);
            if (index >= 0) {
                notifyItemChanged(index);
            }

        } else {
            if (object.equals(this.selectItem)) {
                this.selectItem = null;
                int oldSelectIndex = selectIndex;
                selectIndex = -1;
                if (oldSelectIndex >= 0) {
                    notifyItemChanged(oldSelectIndex);
                }

                return;
            }
            int oldSelectIndex = data.indexOf(this.selectItem);
            selectIndex = data.indexOf(object);
            this.selectItem = object;
            if (selectIndex >= 0) {
                int index = data.indexOf(object);
                notifyItemChanged(index);
            }

            if (oldSelectIndex >= 0) {
                notifyItemChanged(oldSelectIndex);
            }
        }

    }

    private WidgetActivity activity;

    public BaseRecyclerViewAdapter(WidgetActivity activity) {
        this.activity = activity;
    }

    public boolean isSelected(int index) {
        if (enableMultipleSelect) {
            return selectIndexList.contains(index + "");
        }
        return selectIndex == index;
    }

    public boolean isSelectedItem(T object) {
        if (enableMultipleSelect) {
            return selectItemList.contains(object);
        }
        return object.equals(this.selectItem);
    }


    protected List<T> data = new ArrayList<>();


    public void addData(List<T> list) {
        addData(list, false);
    }

    public void addData(List<T> list, boolean front) {
        if (list != null) {
            if (front) {
                this.addData(list, front);
                notifyItemRangeChanged(0, list.size());

            } else {
                this.data.addAll(list);
                notifyItemRangeChanged(this.data.size() - list.size(), list.size());
            }
        }
    }


    public void setData(List<T> data) {
        if (data == null) {
            this.data.clear();
        } else {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    public void bindData(List<T> data) {
        this.data = data;
    }


    public void addItem(T item) {
        this.data.add(item);
        notifyItemInserted(data.size() - 1);
    }

    public void addItem(int index, T item) {
        this.data.add(index, item);
        Log.d("VideoPublishManager", "add Item: " + index);
        notifyItemInserted(index);
    }

    public T getItem(int position) {
        return data.get(position);
    }

    public boolean remove(T item) {
        int index = data.indexOf(item);
        if (index >= 0) {
            T res = data.remove(index);
            if (res != null) {
                notifyItemRemoved(index);
                return true;
            }
        }
        return false;
    }

    public T remove(int index) {
        T res = data.remove(index);
        if (res != null) {
            notifyItemRemoved(index);
        }
        return res;
    }

    public void updateItem(int index, T item) {
        if (data.size() > index && index >= 0) {
            data.set(index, item);
            notifyItemChanged(index);
        }
    }

    public void updateItem(T item) {
        int index = data.indexOf(item);
        if (index >= 0) {
            data.set(index, item);
            notifyItemChanged(index);

        }

    }


    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewHolderWidget widget = onCreateViewHolderWidget(viewType);
        if (widget != null) {
            widget.setAdapter(this);
            recyclerWidget.loadChildWidget(
                    recyclerWidget.getRecyclerView(),
                    widget.modifier().disableAddView().get()
            );
            return new BaseRecyclerViewHolder(widget);

        }
        return null;
    }


    @Override
    public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BaseRecyclerViewHolder<T> viewHolder = (BaseRecyclerViewHolder) holder;
        bindData(viewHolder.getWidget(), position);
    }


    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        BaseRecyclerViewHolder<T> viewHolder = (BaseRecyclerViewHolder) holder;
        viewHolder.getWidget().onDetachedFromWindow();
    }


    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        BaseRecyclerViewHolder<T> viewHolder = (BaseRecyclerViewHolder) holder;
        viewHolder.getWidget().onAttachedToWindow();
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        BaseRecyclerViewHolder<T> viewHolder = (BaseRecyclerViewHolder) holder;
        viewHolder.getWidget().remove();
    }

    protected void bindData(RecyclerViewHolderWidget<T> widget, int position) {
        widget.bindData(position, getItem(position));
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    protected abstract RecyclerViewHolderWidget<T> onCreateViewHolderWidget(int viewType);


    public List<T> getData() {
        return data;
    }


}
