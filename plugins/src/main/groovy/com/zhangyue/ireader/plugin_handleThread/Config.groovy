package com.zhangyue.ireader.plugin_handleThread

class Config {

    /**
     * 是否打印日志
     */
    static boolean printLog

    /**
     * 是否启用插件
     */
    static boolean turnOn


    static void logger(String msg) {
        if (!printLog) {
            return
        }
        println msg
    }

}