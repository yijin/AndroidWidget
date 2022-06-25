package com.yj.widget.recycler;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerWidget<K, T> extends BaseRecyclerWidget<T> implements RecyclerDataSource<K, T> {

    protected int prefetchDistance = 5;
    private RecyclerDataSource<K, T> dataSource = this;
    private boolean defaultLoadNext = true;
    private boolean enableLoadNext = true;
    private boolean enableLoadFront = false;

    public void setDataSource(RecyclerDataSource<K, T> dataSource) {
        this.dataSource = dataSource;
    }

    public void setPrefetchDistance(int prefetchDistance) {
        this.prefetchDistance = prefetchDistance;
    }

    public void setDefaultLoadNext(boolean defaultLoadNext) {
        this.defaultLoadNext = defaultLoadNext;
    }

    public void enableLoadNext(boolean enableLoadNext) {
        this.enableLoadNext = enableLoadNext;
    }

    public void enableLoadFront(boolean enableLoadFront) {
        this.enableLoadFront = enableLoadFront;
    }

    public void defaultLoadNext(boolean defaultLoadNext) {
        this.defaultLoadNext = defaultLoadNext;
    }


    @Override
    protected void initRecyclerView(RecyclerView recyclerView) {
        super.initRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(onScrollListener);
    }

    @Override
    protected void onCreatedView() {
        super.onCreatedView();
        if (defaultLoadNext) {
            onLoadNext();
        } else {
            onLoadFront();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.removeOnScrollListener(onScrollListener);
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);


        }

        private int findMax(int[] lastPositions) {
            int max = lastPositions[0];
            for (int value : lastPositions) {
                if (value > max) {
                    max = value;
                }
            }

            return max;
        }

        private int findMin(int[] positions) {
            int min = positions[0];
            for (int value : positions) {
                if (value < min) {
                    min = value;
                }
            }

            return min;
        }

        int[] lastPositions = null;
        int[] firstPositions = null;

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int lastVisibleItemPosition = 0;
            int firstVisibleItemPosition = 0;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();


            } else if (layoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();


            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                if (firstPositions == null) {
                    firstPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                staggeredGridLayoutManager.findFirstVisibleItemPositions(firstPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                firstVisibleItemPosition = findMin(firstPositions);
            }

            int visibleItemCount = recyclerView.getChildCount();
            if (lastVisibleItemPosition >= getItemCount() - prefetchDistance
                    && visibleItemCount > 0 && getItemCount() > prefetchDistance
            ) {
                onLoadNext();
            }
            if (firstVisibleItemPosition < 6) {
                onLoadFront();
            }

        }
    };

    private boolean loadingNext = false;
    private K curNextKey = null;
    private boolean loadingFront = false;
    private K curFrontKey = null;
    private boolean hasFirstLoad = false;

    private boolean loadingRefresh = false;


    public void refresh() {
        if (loadingRefresh || dataSource == null) {
            return;
        }
        loadingRefresh = true;
        LoadParams params = new LoadParams(null, true);

        dataSource.onLoad(params, new DefaultLoadCallBack(params, true));

    }

    protected void onLoadNext() {
        if (loadingNext || dataSource == null || !enableLoadNext) {
            return;
        }

        if (curNextKey != null || (!hasFirstLoad && defaultLoadNext)) {
            loadingNext = true;
            LoadParams params = new LoadParams(curNextKey, true);

            dataSource.onLoad(params, new DefaultLoadCallBack(params, false));
        }
    }

    protected void onLoadFront() {
        if (!enableLoadFront || loadingFront || dataSource == null) {
            return;
        }
        if (curFrontKey != null || (!hasFirstLoad && !defaultLoadNext)) {
            loadingFront = true;
            LoadParams params = new LoadParams(curFrontKey, false);
            dataSource.onLoad(params, new DefaultLoadCallBack(params, false));
        }
    }

    @Override
    public void onLoad(LoadParams<K> params, LoadCallBack<K, T> callBack) {

    }

    private List<DefaultLoadCallBack> curLoadCallBackList = new ArrayList<>();

    private class DefaultLoadCallBack implements LoadCallBack<K, T> {
        private LoadParams<K> loadParams;
        private boolean isRefresh;


        public DefaultLoadCallBack(LoadParams<K> loadParams, boolean isRefresh) {
            this.loadParams = loadParams;
            this.isRefresh = isRefresh;
        }

        @Override
        public void onLoadError(String msg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isRefresh) {
                        loadingRefresh = false;
                        loadingNext = false;
                        loadingFront = false;
                        curLoadCallBackList.clear();
                    } else if (loadParams.isLoadNext()) {
                        loadingNext = false;
                    } else {
                        loadingFront = false;
                    }

                }
            });


        }

        @Override
        public void onLoadSuccess(List<T> list, K newKey) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hasFirstLoad = true;
                    if (isRefresh) {
                        loadingRefresh = false;
                        loadingNext = false;
                        loadingFront = false;
                        curLoadCallBackList.clear();
                        if (loadParams.isLoadNext()) {
                            curNextKey = newKey;
                        } else {
                            curFrontKey = newKey;
                        }
                        setData(list);

                    } else if (loadParams.isLoadNext()) {
                        loadingNext = false;
                        curNextKey = newKey;
                        addData(list);
                    } else {
                        loadingFront = false;
                        curFrontKey = newKey;
                        addData(list, true);
                    }
                }
            });


        }
    }


}
