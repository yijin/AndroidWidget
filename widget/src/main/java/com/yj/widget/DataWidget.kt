package com.yj.widget

import androidx.lifecycle.Lifecycle

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/6/5
 *     desc  :
 * </pre>
 */
open class DataWidget : BaseWidget() {

    lateinit var parentWidget: BaseWidget

    fun create(widgetManager: WidgetManager, parentWidget: BaseWidget) {
        super.initWidget(widgetManager, parentWidget.page)
        this.parentWidget = parentWidget
        page.pageAllWidgets.add(this)
        parentWidget.childWidgets.add(this)
        currentState == WidgetState.CREATED
        onCreate(widgetManager.savedInstanceState)
        if (page != null) {
            when (page?.currentState) {
                WidgetState.STARTED -> {
                    start()
                }
                WidgetState.RESUMED -> {
                    start()
                    resume()
                }
            }
        } else {
            when (activity.lifecycle.currentState) {

                Lifecycle.State.STARTED -> {
                    start()
                }
                Lifecycle.State.RESUMED -> {
                    start()
                    resume()
                }
            }
        }
    }


    private fun start() {
        if (currentState == WidgetState.CREATED || currentState == WidgetState.STOP) {
            onStart()
            currentState = WidgetState.STARTED

        }
    }

    private fun resume() {

        if (currentState == WidgetState.STARTED || currentState == WidgetState.PAUSE) {
            onResume()
            currentState = WidgetState.RESUMED
        }

    }

    private fun pause() {
        if (currentState == WidgetState.RESUMED) {
            currentState = WidgetState.PAUSE
            onPause()

        }

    }

    private fun stop() {

        if (currentState == WidgetState.PAUSE) {
            currentState = WidgetState.STOP
            onStop()

        }

    }


    private fun destroy() {
        if (currentState == WidgetState.STOP) {
            onDestroy()
            currentState = WidgetState.DESTROYED

        }


    }

    override fun remove() {
        super.remove()
        parentWidget?.childWidgets?.remove(this)

        when (currentState) {

            WidgetState.STARTED -> {
                stop()
                destroy()

            }
            WidgetState.RESUMED -> {
                pause()
                stop()
                destroy()

            }
            WidgetState.PAUSE -> {
                stop()
                destroy()
            }
            WidgetState.STOP -> {
                destroy()
            }
            else -> {
                destroy()
            }
        }
    }

    override fun postAction(action: WidgetAction) {

        when (action) {


            WidgetAction.ACTION_START -> {
                start()
            }
            WidgetAction.ACTION_RESUME -> {
                resume()
            }
            WidgetAction.ACTION_PAUESE -> {
                pause()
            }
            WidgetAction.ACTION_STOP -> {
                stop()
            }
            WidgetAction.ACTION_DESTROY -> {
                destroy()
            }

        }


    }

    override fun backPressed() {
        widgetManager.backPressed()
    }


}