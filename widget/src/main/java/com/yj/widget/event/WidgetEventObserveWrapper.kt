package com.yj.widget.event

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
class WidgetEventObserveWrapper(
    val key: String,
    val observe: WidgetEventObserve<*>,
    val classType: Class<*>
) {

    override fun equals(other: Any?): Boolean {
        if (other is WidgetEventObserveWrapper) {
            key == other.key && classType == other.classType
        }
        return false
    }

}