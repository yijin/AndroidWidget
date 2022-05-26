package com.yj.widget


import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.*
import com.yj.widget.page.PageWidgetManager
import com.yj.widget.page.PageWidgetStartBuilder
import kotlin.collections.ArrayList


class WidgetManager : DefaultLifecycleObserver {


    lateinit var activity: WidgetActivity

    fun initWidgetManager(activity: WidgetActivity) {

        this.activity = activity
        activity.lifecycle.addObserver(this)
    }


    /**
     * activity 通用的dataWidget
     */
    private val activityDataWidgets: MutableList<DataWidget> = ArrayList()

    /**
     * 不在pageWidget里面的widget
     */
    private val activtyWidgets: MutableList<Widget> = ArrayList()

    /**
     * pageWidget的
     */
    private val pageWidgetManager = PageWidgetManager(this)


    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_START)
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_START)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_START)
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_RESUME)
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_RESUME)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_RESUME)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_PAUESE)
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_PAUESE)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_PAUESE)
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_STOP)
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_STOP)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_STOP)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        pageWidgetManager.destroy()
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_DESTROY)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_DESTROY)
        }


    }

    fun topPageWidget(): Widget? {

        return pageWidgetManager.topPageWidget()
    }

    fun restore() {
        pageWidgetManager.restore()
        activityDataWidgets.forEach {
            it.postAction(WidgetAction.ACTION_CREATED)
        }
        activtyWidgets.forEach {
            it.postAction(WidgetAction.ACTION_CREATED)
        }

    }


    fun loadChild(
        parentView: ViewGroup?,
        widget: Widget,
        parentWidget: BaseWidget
    ): Boolean {
        return WidgetLoadBuilder(this, widget).setPageWidget(parentWidget.pageWidget)
            .setParentWidget(parentWidget).setParentView(parentView).load()
    }

    fun loadDataWidget(parentWidget: BaseWidget, widget: DataWidget): Boolean {
        return WidgetLoadBuilder(this, widget).setPageWidget(parentWidget?.pageWidget)
            .setParentWidget(parentWidget).load()

    }

    internal fun loadActivityDataWidget(widget: DataWidget) {
        WidgetLoadBuilder(this, widget).load()
        activityDataWidgets.add(widget)
    }

    internal fun loadActivityWidget(parentView: ViewGroup, widget: Widget) {
        WidgetLoadBuilder(this, widget).setParentView(parentView).load()
        activtyWidgets.add(widget)
    }

    fun replaceWidget(
        oldWidget: Widget,
        newWidget: Widget
    ): Boolean {
        if (oldWidget.isPageWidget) {
            return pageWidgetManager.replaceWidget(oldWidget, newWidget)

        } else {
            var index = -1;
            if (oldWidget.contentView != null && oldWidget.parentView != null) {
                index = oldWidget!!.parentView!!.indexOfChild(oldWidget.contentView)
            }
            val builder = WidgetLoadBuilder(
                this,
                newWidget
            ).setPageWidget(if (oldWidget.isPageWidget) newWidget else oldWidget.pageWidget)
                .setParentWidget(oldWidget.parentWidget).setParentView(oldWidget.parentView)
                .setIndex(index)
            oldWidget.removeSelf()

            return builder.load()
        }
    }

    internal fun setActivityPage(widget: Widget) {
        pageWidgetManager.startClearAll(widget).startNoAnim()
    }

    internal fun startPageWidget(
        widget: Widget
    ): PageWidgetStartBuilder {
        return pageWidgetManager.start(widget)
    }


    internal fun startPageWidgetClearAll(
        widget: Widget
    ): PageWidgetStartBuilder {
        return pageWidgetManager.startClearAll(widget)
    }

    fun backFirstWidget(classType: Class<*>): Boolean {
        return pageWidgetManager.backFirstWidget(classType)
    }

    fun backLastWidget(classType: Class<*>): Boolean {
        return pageWidgetManager.backLastWidget(classType)
    }

    internal fun startPageWidgetSingleTask(
        widget: Widget
    ): PageWidgetStartBuilder {
        return pageWidgetManager.startSingleTask(widget)
    }

    internal fun startPageWidgetSingleTop(
        widget: Widget
    ): PageWidgetStartBuilder {
        return pageWidgetManager.startSingleTop(widget)
    }


    fun remove(widget: BaseWidget) {
        activtyWidgets.remove(widget)
        activityDataWidgets.remove(widget)
        pageWidgetManager.widgetList.remove(widget)
    }

    fun containsPageWidget(widget: Widget): Boolean {

        return pageWidgetManager.widgetList.contains(widget)
    }


    fun onConfigurationChanged(newConfig: Configuration) {

        pageWidgetManager.topPageWidget()?.onConfigurationChanged(newConfig)

        activtyWidgets.forEach {
            it.onConfigurationChanged(newConfig)
        }
        activityDataWidgets.forEach {
            it.onConfigurationChanged(newConfig)
        }
    }


    fun onSaveInstanceState(outState: Bundle) {

        pageWidgetManager.topPageWidget()?.onSaveInstanceState(outState)

        activtyWidgets.forEach {
            it.onRestoreInstanceState(outState)
        }
        activityDataWidgets.forEach {
            it.onRestoreInstanceState(outState)
        }
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        pageWidgetManager.topPageWidget()?.onRestoreInstanceState(savedInstanceState)

        activtyWidgets.forEach {
            it.onRestoreInstanceState(savedInstanceState)
        }
        activityDataWidgets.forEach {
            it.onRestoreInstanceState(savedInstanceState)
        }
    }

    fun inTopPageWidget(widget: Widget?): Boolean {
        if (widget == null) {
            return false
        }
        return pageWidgetManager.widgetList.peekLast() == widget
    }

    fun onBackPressed(): Boolean {
        return pageWidgetManager.backPressed()
    }


}