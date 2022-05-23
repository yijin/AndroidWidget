package com.yj.widget.event


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