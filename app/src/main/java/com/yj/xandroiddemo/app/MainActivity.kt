package com.yj.xandroiddemo.app

import android.os.Bundle
import android.util.Log
import android.view.View
import com.yj.widget.DataWidget
import com.yj.widget.Widget
import com.yj.widget.WidgetActivity
import com.yj.widget.event.WidgetEventObserve
import com.yj.xandroiddemo.app.databinding.Widget1Binding
import com.yj.xandroiddemo.app.databinding.Widget2Binding
import com.yj.xandroiddemo.app.databinding.Widget3Binding


class MainActivity : WidgetActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPageWidget(Widget1())


        /* observeEvent(object :WidgetEventObserve<String>{
             override fun onEvent(event: String) {
                 Log.d("yijinsb","onEvent string "+event)

             }
         })

         observeEvent(object :WidgetEventObserve<Int>{
             override fun onEvent(event: Int) {
                 Log.d("yijinsb","onEvent int "+event)
             }
         })
         postEvent("abc")
         postEvent(124)*/
        postStickyEvent("stickyEvent")
        postDelayEvent("abc", 1000)

        observeEvent(String::class.java, object : WidgetEventObserve<String> {
            override fun onChange(event: String) {
                Log.d("yijinsb", "string event " + event)

            }
        })
        observeStickyEvent(String::class.java, object : WidgetEventObserve<String> {
            override fun onChange(event: String) {
                Log.d("yijinsb", "string sticky event= " + event)

            }
        })

        loadDataWidget(MyDataWidget())


    }

    class MyDataWidget : DataWidget() {
        override fun onCreate() {
            super.onCreate()
        }
    }


    class Widget1 : Widget() {
        private lateinit var binding: Widget1Binding

        override fun onLoadContentView(): View {
            binding = Widget1Binding.inflate(layoutInflater)
            return binding.root
        }

        override fun onCreatedView() {
            super.onCreatedView()
            binding.btn1.setOnClickListener({
                startPageWidget(Widget2()).start()
            })


        }
    }

    class Widget2 : Widget() {
        private lateinit var binding: Widget2Binding
        override fun onLoadContentView(): View {
            binding = Widget2Binding.inflate(layoutInflater)
            return binding.root
        }

        override fun onCreatedView() {
            super.onCreatedView()
            binding.btn1.setOnClickListener({
                startPageWidget(Widget3()).start()
            })
            binding.back.setOnClickListener {
                backPressed()
            }


        }
    }

    class Widget3 : Widget() {
        private lateinit var binding: Widget3Binding
        override fun onLoadContentView(): View {
            binding = Widget3Binding.inflate(layoutInflater)
            return binding.root
        }

        override fun onCreatedView() {
            super.onCreatedView()
            binding.back.setOnClickListener {
                backPressed()
            }
            binding.backWidget1.setOnClickListener {
                //backLastWidget(Widget1::class.java)
                startPageWidgetSingleTask(Widget1()).start()
            }
        }

    }


}