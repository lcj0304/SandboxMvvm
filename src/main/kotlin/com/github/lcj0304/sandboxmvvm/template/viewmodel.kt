package com.github.lcj0304.sandboxmvvm.template

/**
 * @description ：
 * @author :liucj
 * @date : 2023/4/28 10:58
 */
fun viewModelTemplate(
    packageName: String,
    modelName: String,
    desc: String = "TODO:",
    isListViewModel: Boolean = false,
    isDiffList:Boolean = false,
    entityName:String = "Any",
    moreInfo: MoreInfo = MoreInfo()
): String {
    var listField = ""
    var diffImport = ""
    var diff = ""
    if (isListViewModel) {
        listField = """val listLayout = ${modelName.getListLayoutName()}()
            val listModel = ${modelName.getListModelName()}(context)
        """.trimMargin()

        if (isDiffList) {
            diffImport = "import androidx.recyclerview.widget.DiffUtil"
            diff = getDiff(entityName)
        }
    }

    val viewModelFullName = if (moreInfo.isEventPage) {
        "com.sandboxol.center.view.activity.event.BaseEventViewModel"
    } else {
        "com.sandboxol.common.base.app.mvvm.BundleViewModel"
    }

    val viewModelName = if (moreInfo.isEventPage) {
        "BaseEventViewModel"
    } else {
        "BundleViewModel"
    }


    return """
package $packageName
       
import android.app.Application
import android.os.Bundle
$diffImport
import com.sandboxol.common.base.app.mvvm.BaseModel
import $viewModelFullName
        
${getFileComments(desc)}   
class ${modelName}VM(context: Application, bundle:Bundle?):${viewModelName}<BaseModel>(context, bundle) {
    val uc = UIObservable()
    $listField
    init {
        initMessenger()
    }
    
    override fun initMessenger() {
    
    }
    
    
    
    class UIObservable {
    
    }
    
$diff    
}
""".trimIndent()
}
