package com.github.lcj0304.sandboxmvvm.template




fun listFileStr(
    modulePackageName: String,
    packageName: String,
    modelName: String,
    isPageList:Boolean = false,
    entityPackage:String = "",
    entityName:String = "",
): String {
 val desc = ""
    val entity = entityName.ifEmpty {
        "Any"
    }

    var baseListModelImport = "import com.sandboxol.common.widget.rv.datarv.DataListModel"
    var baseListModel = "DataListModel"
    var onLoadDataSrc = """    override fun onLoadData(listener: OnResponseListener<List<${entity}>>?) {
        
    }"""


    if (isPageList) {
        baseListModel = "PageListModel"
        baseListModelImport = """import com.sandboxol.common.widget.rv.pagerv.PageListModel
            import com.sandboxol.common.widget.rv.pagerv.PageData"""
        onLoadDataSrc = """    override fun onLoadData(page: Int, size: Int, listener: OnResponseListener<PageData<${entity}>>?) {
        
    }"""
    }





    return """
package $packageName    

import ${modulePackageName}.R
import com.sandboxol.common.widget.rv.BaseListLayout

import android.content.Context
import ${modulePackageName}.BR
import com.sandboxol.common.base.viewmodel.ItemBinder
import com.sandboxol.common.base.viewmodel.ListItemViewModel
import com.sandboxol.common.base.web.OnResponseListener
$baseListModelImport
 
${getFileComments(desc)}
class ${modelName.getListLayoutName()} : BaseListLayout() {

    override fun getLayoutId(): Int {
        return R.layout.${modelName.getListLayoutXmlName()}
    }
}

    
${getFileComments(desc)}
class ${modelName.getListModelName()}(val context: Context?) : ${baseListModel}<${entity}>(context) {
    
    override fun onItemBind(itemBinder: ItemBinder, position: Int, item: ListItemViewModel<${entity}>?) {
        itemBinder.bindItem(BR.ViewModel, R.layout.${modelName.getListLayoutItemXmlName()})
    }

    override fun getItemViewModel(item: ${entity}?): ListItemViewModel<${entity}> {
        return ${modelName.getListItemViewModelName()}(context, item)
    }

    $onLoadDataSrc
}
    
${getFileComments(desc)}
class ${modelName.getListItemViewModelName()}(var context: Context?, item: ${entity}?) : ListItemViewModel<${entity}>(context, item) {

    init {
       
    }
}
      
""".trimIndent()
}