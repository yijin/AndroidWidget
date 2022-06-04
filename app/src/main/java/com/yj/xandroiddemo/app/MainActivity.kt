package com.yj.xandroiddemo.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.yj.widget.DataWidget
import com.yj.widget.Widget
import com.yj.widget.WidgetActivity
import com.yj.widget.event.WidgetEventObserve
import com.yj.widget.page.Page
import com.yj.xandroiddemo.app.databinding.Widget1Binding
import com.yj.xandroiddemo.app.databinding.Widget2Binding
import com.yj.xandroiddemo.app.databinding.Widget2Child1Binding
import com.yj.xandroiddemo.app.databinding.Widget3Binding
import kotlinx.android.parcel.Parcelize


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


            setPage(Page1())
        }


    }


    @Parcelize
    class Page1 : Page() {
        override fun build(): Widget {
            return Widget1().matchParent().backgroundColor(Color.WHITE).get()
        }
    }

    class Widget1 : Widget() {
        private lateinit var binding: Widget1Binding

        override fun onCreateView(container: ViewGroup?): View {

            binding = Widget1Binding.inflate(layoutInflater)
            binding.btn1.setOnClickListener({
                startPage(Page2(3)).start()
            })
            return binding.root
        }

    }

    @Parcelize
    class Page2(var num: Int) : Page() {
        override fun build(): Widget {
            return Widget2()
                .matchParent()
                .backgroundColor(Color.WHITE)
                .get()
        }

        inner class Widget2 : Widget() {
            private lateinit var binding: Widget2Binding
            override fun onCreateView(container: ViewGroup?): View {
                Log.d("yijin2b", "Widget2 onCreateView ")

                binding = Widget2Binding.inflate(layoutInflater)
                binding.btn1.setOnClickListener({
                    startPage(Page3()).start()
                })
                binding.back.setOnClickListener {
                    backPressed()
                }
                binding.jumNextActivity.setOnClickListener {
                    activity.startActivity(Intent(activity, TestActivity1::class.java))
                }
                loadChildWidget(
                    binding.box,
                    Widget2Child()
                        .margin(30)
                        .backgroundColor(Color.BLUE)
                        .width(800)
                        .padding(30)
                        .weight(1f).get()
                )
                return binding.root
            }

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                Log.d("yijin2b", "Widget2 onCreate ")
            }

            override fun onStart() {
                super.onStart()
                Log.d("yijin2b", "Widget2 onStart ")
            }

            override fun onResume() {
                super.onResume()
                Log.d("yijin2b", "Widget2 onResume ")
            }

            override fun onPause() {
                super.onPause()
                Log.d("yijin2b", "Widget2 onPause ")
            }

            override fun onStop() {
                super.onStop()
                Log.d("yijin2b", "Widget2 onStop ")
            }

            override fun onDestroyView() {
                super.onDestroyView()
                Log.d("yijin2b", "Widget2 onDestroyView ")
            }

            override fun onDestroy() {
                super.onDestroy()
                Log.d("yijin2b", "Widget2 onDestroy ")
            }


        }

        inner class Widget2Child : Widget() {
            private lateinit var binding: Widget2Child1Binding


            override fun onCreateView(container: ViewGroup?): View {
                Log.d("yijinsb", "Widget2Child onCreateView ")

                binding = Widget2Child1Binding.inflate(layoutInflater)
                binding.text.text = "Widget2Child  num=${num}"
                binding.text.setOnClickListener {
                    num++
                    binding.text.text = "Widget2Child  num=${num}"
                }
                return binding.root
            }

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                Log.d("yijinsb", "Widget2Child onCreate ")
            }

            override fun onStart() {
                super.onStart()
                Log.d("yijinsb", "Widget2Child onStart ")
            }

            override fun onResume() {
                super.onResume()
                Log.d("yijinsb", "Widget2Child onResume ")
            }

            override fun onPause() {
                super.onPause()
                Log.d("yijinsb", "Widget2Child onPause ")
            }

            override fun onStop() {
                super.onStop()
                Log.d("yijinsb", "Widget2Child onStop ")
            }

            override fun onDestroyView() {
                super.onDestroyView()
                Log.d("yijinsb", "Widget2Child onDestroyView ")
            }

            override fun onDestroy() {
                super.onDestroy()
                Log.d("yijinsb", "Widget2Child onDestroy ")
            }


        }

    }


    @Parcelize
    class Page3 : Page() {
        override fun build(): Widget {
            return Widget3()
                .matchParent()
                .backgroundColor(Color.WHITE)
                .get()
        }
    }

    class Widget3 : Widget() {
        private lateinit var binding: Widget3Binding
        override fun onCreateView(container: ViewGroup?): View {

            binding = Widget3Binding.inflate(layoutInflater)
            binding.back.setOnClickListener {
                backPressed()
            }
            binding.backWidget1.setOnClickListener {
                startPageSingleTask(Page1()).start()
            }
            return binding.root
        }


    }


}