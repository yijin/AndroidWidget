package com.yj.widget


import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.lifecycle.*
import com.yj.widget.page.PageWidgetManager
import com.yj.widget.page.PageWidgetStartBuilder


class WidgetManager : DefaultLifecycleObserver {


    lateinit var activity: WidgetActivity

    var savedInstanceState: Bundle? = null

    fun initWidgetManager(activity: WidgetActivity, savedInstanceState: Bundle?) {

        this.activity = activity
        this.savedInstanceState = savedInstanceState
        activity.lifecycle.addObserver(this)
    }


    /**
     * pageWidget的
     */
    private val pageWidgetManager = PageWidgetManager(this)


    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_START)

    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_RESUME)

    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_PAUESE)

    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        pageWidgetManager.topPageWidget()?.postAction(WidgetAction.ACTION_STOP)

    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        if (!activity.isChangingConfigurations || !activity.needSaveWidgets()) {
            pageWidgetManager.destroy()
        }


    }

    fun topPageWidget(): Widget? {

        return pageWidgetManager.topPageWidget()
    }

    fun pageSize(): Int {
        return pageWidgetManager.pageSize()
    }

    fun restoreConfigurationChange() {
        pageWidgetManager.restoreConfigurationChange()

    }

    fun restoreNoConfigurationChange(savedInstanceState: Bundle) {
        pageWidgetManager.restoreNoConfigurationChange(savedInstanceState)
    }


    fun loadChild(
        parentView: ViewGroup,
        widget: Widget,
        parentWidget: Widget
    ): Boolean {
        return WidgetLoadBuilder(
            this,
            widget,
            parentView,
            parentWidget.pageWidget,
            parentWidget
        ).load()
    }

    fun loadDataWidget(parentWidget: BaseWidget, widget: DataWidget): Boolean {
        if (activity.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return false
        }
        widget.create(this, parentWidget)
        return true


    }


    fun replaceWidget(
        oldWidget: Widget,
        newWidget: Widget
    ): Boolean {
        if (oldWidget.isPageWidget) {
            return throw RuntimeException("PageWidget cannot replace")

        } else {
            var index = -1;
            if (oldWidget.contentView != null && oldWidget.parentView != null) {
                index = oldWidget!!.parentView!!.indexOfChild(oldWidget.contentView)
            }
            val builder = WidgetLoadBuilder(
                this,
                newWidget.index(index).get(),
                oldWidget.parentView!!,
                oldWidget.pageWidget,
                oldWidget.parentWidget as Widget
            )
            oldWidget.removeSelf()

            return builder.load()
        }
    }

    internal fun setActivityPage(classType: Class<out Widget>, bundle: Bundle?) {
        pageWidgetManager.startClearAll(classType, bundle).startNoAnim()
    }

    internal fun startPageWidget(
        classType: Class<out Widget>, bundle: Bundle?
    ): PageWidgetStartBuilder {
        return pageWidgetManager.start(classType, bundle)
    }


    internal fun startPageWidgetClearAll(
        classType: Class<out Widget>, bundle: Bundle?
    ): PageWidgetStartBuilder {
        return pageWidgetManager.startClearAll(classType, bundle)
    }

    fun backFirstWidget(classType: Class<out Widget>): Boolean {
        return pageWidgetManager.backFirstWidget(classType)
    }

    fun backLastWidget(classType: Class<out Widget>): Boolean {
        return pageWidgetManager.backLastWidget(classType)
    }

    internal fun startPageWidgetSingleTask(
        classType: Class<out Widget>, bundle: Bundle?
    ): PageWidgetStartBuilder {
        return pageWidgetManager.startSingleTask(classType, bundle)
    }

    internal fun startPageWidgetSingleTop(
        classType: Class<out Widget>, bundle: Bundle?
    ): PageWidgetStartBuilder {
        return pageWidgetManager.startSingleTop(classType, bundle)
    }


    fun remove(widget: BaseWidget) {

        if (widget is Widget) {
            if (widget.isPageWidget) {
                pageWidgetManager.removePage(widget)
            }
        }
    }

    fun containsPageWidget(widget: Widget): Boolean {

        return pageWidgetManager.containsPageWidget(widget)
    }


    fun onConfigurationChanged(newConfig: Configuration) {

        pageWidgetManager.topPageWidget()?.onConfigurationChanged(newConfig)


    }


    fun onSaveInstanceState(outState: Bundle) {

        pageWidgetManager.onSaveInstanceState(outState)


    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        pageWidgetManager.onRestoreInstanceState(savedInstanceState)

    }

    fun inTopPageWidget(widget: Widget?): Boolean {
        if (widget == null) {
            return false
        }
        return pageWidgetManager.topPageWidget() == widget
    }

    fun onBackPressed(): Boolean {
        return pageWidgetManager.backPressed()
    }


}