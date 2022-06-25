package com.yj.widget.recycler;

/**
 * @CreateTime : 2022/6/20 4:52 下午
 * @Author : yijin.yi
 * @Description :
 **/
public interface RecyclerDataSource<K, T> {
    void onLoad(LoadParams<K> params, LoadCallBack<K, T> callBack);
}
