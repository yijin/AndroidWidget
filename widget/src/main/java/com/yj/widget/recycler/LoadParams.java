package com.yj.widget.recycler;

/**
 * @CreateTime : 2022/6/20 5:07 下午
 * @Author : yijin.yi
 * @Description :
 **/
public class LoadParams<K> {

    private K key;
    private boolean loadNext;

    public LoadParams(K key, boolean isLoadNext) {
        this.key = key;
        this.loadNext = isLoadNext;
    }

    public K getKey() {
        return key;
    }

    public boolean isLoadNext() {
        return loadNext;
    }
}
