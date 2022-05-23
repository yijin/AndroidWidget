package com.yj.widget

import androidx.lifecycle.Lifecycle

open class DataWidget : BaseWidget() {
    override fun create(params: WidgetCreateParams) {
        super.create(params)
        params.pageWidget?.pageAllWidgets?.add(this)
        currentState == WidgetState.CREATED
        onCreate()
        if (pageWidget != null) {
            when (pageWidget?.currentState) {
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
            currentState = WidgetState.STARTED

            onStart()
        }
    }

    private fun resume() {

        if (currentState == WidgetState.STARTED || currentState == WidgetState.PAUSE) {
            currentState = WidgetState.RESUMED
            onResume()
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
            currentState = WidgetState.DESTROYED
            onDestroy()
        }


    }

    override fun removeSelf() {
        super.removeSelf()

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



}