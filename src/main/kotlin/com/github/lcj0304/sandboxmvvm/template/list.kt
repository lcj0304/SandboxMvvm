package com.github.lcj0304.sandboxmvvm.template




fun listFileStr(
    modulePackageName: String,
    packageName: String,
    modelName: String,
    moreInfo: MoreInfo,
    entityPackage:String = "",
): String {
 val desc = ""
    var entity = moreInfo.entityName.ifEmpty {
        "Any"
    }


    // 如何是任务列表，则使用 BaseTask 作为实体类
    // 导入对应的包
    var taskClassImport = ""
    var listArgsDef = ""
    var listArgs = ""
    var listArgsItemViewDef = ""
    var parentItemViewModel = "ListItemViewModel"
    var itemViewModelContextDef = "var context: Context"
    if (moreInfo.isTaskList) {
        entity = "BaseTask"

        taskClassImport = """
import com.sandboxol.center.entity.task.BaseTask
import com.sandboxol.center.view.viewmodel.event.BaseTaskItemViewModel
import com.sandboxol.center.entity.Reward
        """.trimIndent()

        // 以下参数定义，按钮点击事件和奖励点击事件
        // listModel 传递参数定义
        listArgsDef = """, val onButtonClickListener: ((BaseTask) -> Unit)? = null,
    val onItemClickListener: ((Reward) -> Unit)? = null,"""
        //任务的 itemViewmodel 传递参数定义
        listArgsItemViewDef = """onButtonClickListener: ((BaseTask) -> Unit)? = null,
            onItemClickListener: ((Reward) -> Unit)? = null,""".trimMargin()
        // 传递到BaseTaskItemViewModel 的参数
        listArgs = """, onButtonClickListener, onItemClickListener"""
        parentItemViewModel = "BaseTaskItemViewModel"
        itemViewModelContextDef = "context: Context"
    }



    var baseListModelImport = "import com.sandboxol.common.widget.rv.datarv.DataListModel"
    var baseListModel = "DataListModel"
    var onLoadDataSrc = """    override fun onLoadData(listener: OnResponseListener<List<${entity}>>?) {
        
    }"""


    if (moreInfo.isPageList) {
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
$taskClassImport
 
${getFileComments(desc)}
class ${modelName.getListLayoutName()} : BaseListLayout() {

    override fun getLayoutId(): Int {
        return R.layout.${moreInfo.listLayoutXmlName}
    }
}

    
${getFileComments(desc)}
class ${modelName.getListModelName()}(val context: Context${listArgsDef}) : ${baseListModel}<${entity}>(context) {
    
    override fun onItemBind(itemBinder: ItemBinder, position: Int, item: ListItemViewModel<${entity}>?) {
        itemBinder.bindItem(BR.ViewModel, R.layout.${moreInfo.itemLayoutXmlName})
    }

    override fun getItemViewModel(item: ${entity}): ListItemViewModel<${entity}> {
        return ${modelName.getListItemViewModelName()}(context, item${listArgs})
    }

    $onLoadDataSrc
}
    
${getFileComments(desc)}
class ${modelName.getListItemViewModelName()}(${itemViewModelContextDef}, 
    item: ${entity},
    ${listArgsItemViewDef}
) : 
    ${parentItemViewModel}<${entity}>(context, item${listArgs}) {

    init {
       
    }
}
      
""".trimIndent()
}

// 任务的列表，模板参考这个文件：
//ActiveTaskDialogList.kt