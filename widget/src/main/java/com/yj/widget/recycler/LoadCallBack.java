package com.yj.widget.recycler;

import java.util.List;

/**
 * @CreateTime : 2022/6/19 8:32 下午
 * @Author : yijin.yi
 * @Description :
 **/
public interface LoadCallBack<K, T> {

    void onLoadError(String msg);

    void onLoadSuccess(List<T> list, K newKey);
}
