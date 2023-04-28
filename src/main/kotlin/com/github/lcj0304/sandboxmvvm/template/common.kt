package com.github.lcj0304.sandboxmvvm.template

import java.text.SimpleDateFormat
import java.util.*


fun getFileComments(desc: String): String {
    return """/**
* @description ：${desc}
* @author :
* @date : ${getCurrentDataTime()}
*/"""
}


fun getCurrentDataTime(): String {
    return SimpleDateFormat("yyyy/MM/dd HH:mm").format(Date())
}

fun getModulePackageName(str:String) :String{
    val array = str.split(".")
    return if (array.size > 3) {
        "${array[0]}.${array[1]}.${array[2]}"
    } else {
        str
    }
}