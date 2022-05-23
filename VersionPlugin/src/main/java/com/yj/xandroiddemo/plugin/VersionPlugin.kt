package com.yj.xandroiddemo.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <pre>
 *     author: yijin
 *     date  : 2022/5/05
 *     desc  : 如果出现红色警告可以忽略，不会影响项目的编译和运行
 * </pre>
 */
class VersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
    }

    companion object {


    }
}
