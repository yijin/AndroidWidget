package com.yj.xandroiddemo.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.yj.widget.DataWidget
import com.yj.widget.Widget
import com.yj.widget.WidgetActivity
import com.yj.widget.event.WidgetEventObserve
import com.yj.xandroiddemo.app.databinding.Widget1Binding
import com.yj.xandroiddemo.app.databinding.Widget2Binding
import com.yj.xandroiddemo.app.databinding.Widget2Child1Binding
import com.yj.xandroiddemo.app.databinding.Widget3Binding


class MainActivity : WidgetActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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

        if (hasRestored) {

        } else {


            loadDataWidget(MyDataWidget())
            setPageWidget(Widget1())
        }


    }

    class MyDataWidget : DataWidget() {
        override fun onCreate() {
            super.onCreate()
        }
    }


    class Widget1 : Widget() {
        private lateinit var binding: Widget1Binding

        override fun onCreateView(): View {
            binding = Widget1Binding.inflate(layoutInflater)
            return binding.root
        }

        override fun onStartView() {
            super.onStartView()
            binding.btn1.setOnClickListener({
                startPageWidget(Widget2()).start()
            })


        }
    }

    class Widget2 : Widget() {
        private lateinit var binding: Widget2Binding
        override fun onCreateView(): View {
            binding = Widget2Binding.inflate(layoutInflater)
            binding.btn1.setOnClickListener({
                startPageWidget(Widget3()).start()
            })
            binding.back.setOnClickListener {
                backPressed()
            }
            binding.jumNextActivity.setOnClickListener {
                activity.startActivity(Intent(activity, TestActivity1::class.java))
            }
            loadChildWidget(binding.box, Widget2Child())
            return binding.root
        }


    }

    class Widget2Child : Widget() {
        private lateinit var binding: Widget2Child1Binding
        private var num = 1
        override fun onCreateView(): View {
            binding = Widget2Child1Binding.inflate(layoutInflater)
            return binding.root
        }

        override fun onStartView() {
            super.onStartView()
            binding.text.text = "Widget2Child  num=${num}"
            binding.text.setOnClickListener {
                num++;
                binding.text.text = "Widget2Child  num=${num}"
            }
        }

    }

    class Widget3 : Widget() {
        private lateinit var binding: Widget3Binding
        override fun onCreateView(): View {
            binding = Widget3Binding.inflate(layoutInflater)
            return binding.root
        }

        override fun onStartView() {
            super.onStartView()
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