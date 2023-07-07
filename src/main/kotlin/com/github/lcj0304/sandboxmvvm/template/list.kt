package com.github.lcj0304.sandboxmvvm.template


/**
 * @description ：
 * @author :liucj
 * @date : 2023/5/4 15:39
 */
fun listLayoutTemplate(
    modulePackageName: String,
    packageName: String,
    modelName: String,
    desc: String = "TODO:",
): String {

    return """
package $packageName    

import ${modulePackageName}.R
import com.sandboxol.common.widget.rv.BaseListLayout

${getFileComments(desc)}
class ${modelName.getListLayoutName()} : BaseListLayout() {

    override fun getLayoutId(): Int {
        return R.layout.${modelName.getListLayoutXmlName()}
    }
}
    """.trimIndent()
}


fun listModelTemplate(
    modulePackageName: String,
    packageName: String,
    modelName: String,
    desc: String = "TODO:",
): String {

    return """
package $packageName    

import android.content.Context
import ${modulePackageName}.BR
import ${modulePackageName}.R
import com.sandboxol.common.base.viewmodel.ItemBinder
import com.sandboxol.common.base.viewmodel.ListItemViewModel
import com.sandboxol.common.base.web.OnResponseListener
import com.sandboxol.common.widget.rv.datarv.DataListModel

${getFileComments(desc)}
class ${modelName.getListModelName()}(val context: Context?) : DataListModel<Any>(context) {
    
    override fun onItemBind(itemBinder: ItemBinder, position: Int, item: ListItemViewModel<Any>?) {
        itemBinder.bindItem(BR.ViewModel, R.layout.${modelName.getListLayoutItemXmlName()})
    }

    override fun getItemViewModel(item: Any?): ListItemViewModel<Any> {
        return ${modelName.getListItemViewModelName()}(context, Any())
    }

    override fun onLoadData(listener: OnResponseListener<List<Any>>?) {
        
    }
}""".trimIndent()
}

/**
 * list item
 * @param modulePackageName String
 * @param packageName String
 * @param modelName String
 * @param desc String
 * @return String
 */
fun listItemTemplate(
    modulePackageName: String,
    packageName: String,
    modelName: String,
    desc: String = "TODO:",
): String {
    return """
package $packageName

import android.content.Context
import com.sandboxol.common.base.viewmodel.ListItemViewModel

${getFileComments(desc)}
class ${modelName.getListItemViewModelName()}(var context: Context?, item: Any?) : ListItemViewModel<Any>(context, item) {

    init {
       
    }
}
    """.trimIndent()
}


fun listFileStr(
    modulePackageName: String,
    packageName: String,
    modelName: String,
    entityPackage:String = "",
    entityName:String = "",
): String {
 val desc = ""

    return """
package $packageName    

import ${modulePackageName}.R
import com.sandboxol.common.widget.rv.BaseListLayout

import android.content.Context
import ${modulePackageName}.BR
import com.sandboxol.common.base.viewmodel.ItemBinder
import com.sandboxol.common.base.viewmodel.ListItemViewModel
import com.sandboxol.common.base.web.OnResponseListener
import com.sandboxol.common.widget.rv.datarv.DataListModel
 
${getFileComments(desc)}
class ${modelName.getListLayoutName()} : BaseListLayout() {

    override fun getLayoutId(): Int {
        return R.layout.${modelName.getListLayoutXmlName()}
    }
}

    
${getFileComments(desc)}
class ${modelName.getListModelName()}(val context: Context?) : DataListModel<Any>(context) {
    
    override fun onItemBind(itemBinder: ItemBinder, position: Int, item: ListItemViewModel<Any>?) {
        itemBinder.bindItem(BR.ViewModel, R.layout.${modelName.getListLayoutItemXmlName()})
    }

    override fun getItemViewModel(item: Any?): ListItemViewModel<Any> {
        return ${modelName.getListItemViewModelName()}(context, Any())
    }

    override fun onLoadData(listener: OnResponseListener<List<Any>>?) {
        
    }
}
    
${getFileComments(desc)}
class ${modelName.getListItemViewModelName()}(var context: Context?, item: Any?) : ListItemViewModel<Any>(context, item) {

    init {
       
    }
}
      
""".trimIndent()
}