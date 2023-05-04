package com.github.lcj0304.sandboxmvvm.template

import com.android.tools.idea.wizard.template.camelCaseToUnderlines
import java.text.SimpleDateFormat
import java.util.*


fun getFileComments(desc: String): String {
    return """/**
* @description ：${desc}
* @author :
* @date : ${getCurrentDataTime()}
*/"""
}


fun String.getListLayoutName(): String {
    return if (this.endsWith("List")) "${this}Layout" else "${this}ListLayout"
}

fun String.getListModelName(): String {
    return if (this.endsWith("List")) "${this}Model" else "${this}ListModel"
}

fun String.getListItemViewModelName(): String {
    return "${this}ItemViewModel"
}

fun String.getListLayoutXmlName(): String {
    return "${camelCaseToUnderlines(this)}_layout"
}

fun String.getListLayoutItemXmlName(): String {
    return "item_${camelCaseToUnderlines(this)}_view"
}

fun getCurrentDataTime(): String {
    return SimpleDateFormat("yyyy/MM/dd HH:mm").format(Date())
}

fun getModulePackageName(str: String): String {
    val array = str.split(".")
    return if (array.size > 3) {
        "${array[0]}.${array[1]}.${array[2]}"
    } else {
        str
    }
}