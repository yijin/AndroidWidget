package com.yj.widget


import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import androidx.lifecycle.*
import com.yj.widget.page.Page
import com.yj.widget.page.PageManager
import com.yj.widget.page.PageStartBuilder


/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
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
    private val pageManager = PageManager(this)


    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        pageManager.topPage()?.postAction(WidgetAction.ACTION_START)

    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        pageManager.topPage()?.postAction(WidgetAction.ACTION_RESUME)

    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        pageManager.topPage()?.postAction(WidgetAction.ACTION_PAUESE)

    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        pageManager.topPage()?.postAction(WidgetAction.ACTION_STOP)

    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        if (!activity.isChangingConfigurations || !activity.needDestroySaveAllPages()) {
            pageManager.topPage()?.activityDestroy()
        }


    }


    fun pageSize(): Int {
        return pageManager.pageSize
    }

    fun restoreConfigurationChange() {
        pageManager.restoreConfigurationChange()

    }

    fun restoreNoConfigurationChange(savedInstanceState: Bundle) {
        pageManager.restoreNoConfigurationChange(savedInstanceState)
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
            parentWidget.page,
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

        var index = -1;
        if (oldWidget.contentView != null && oldWidget.parentView != null) {
            index = oldWidget!!.parentView!!.indexOfChild(oldWidget.contentView)
        }
        val builder = WidgetLoadBuilder(
            this,
            newWidget.modifier().index(index).get(),
            oldWidget.parentView!!,
            oldWidget.page,
            oldWidget.parentWidget as Widget
        )

        oldWidget.removeSelf()
        if (oldWidget.page.rootWidget == oldWidget) {
            oldWidget.page.replaceWidget(newWidget)

        }
        return builder.load()


    }

    internal fun setActivityPage(page: Page) {
        pageManager.startClearAll(page).startNoAnim()
    }

    internal fun startPage(
        page: Page
    ): PageStartBuilder {
        return pageManager.start(page)
    }


    internal fun startPageClearAll(
        page: Page
    ): PageStartBuilder {
        return pageManager.startClearAll(page)
    }

    fun backPage(page: Page): Boolean {
        return pageManager.backPage(page)
    }

    fun backFirstPage(classType: Class<out Page>): Boolean {
        return pageManager.backFirstPage(classType)
    }

    fun backLastPage(classType: Class<out Page>): Boolean {
        return pageManager.backLastPage(classType)
    }

    internal fun startPageSingleTask(
        page: Page
    ): PageStartBuilder {
        return pageManager.startSingleTask(page)
    }

    internal fun startPageSingleTop(
        page: Page
    ): PageStartBuilder {
        return pageManager.startSingleTop(page)
    }


    fun onConfigurationChanged(newConfig: Configuration) {
        pageManager.onConfigurationChanged(newConfig)

    }


    fun onSaveInstanceState(outState: Bundle) {
        pageManager.onSaveInstanceState(outState)


    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        pageManager.onRestoreInstanceState(savedInstanceState)

    }

    internal fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        pageManager.onActivityResult(requestCode, resultCode, data)
    }

    fun inTopPage(page: Page): Boolean {

        return pageManager.topPage() == page
    }

    fun backPressed(): Boolean {
        return pageManager.backPressed()
    }


}