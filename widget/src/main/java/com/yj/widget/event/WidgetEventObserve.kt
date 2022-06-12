package com.yj.widget.event
/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
interface WidgetEventObserve<T> {
    fun onChange(event: T)

}