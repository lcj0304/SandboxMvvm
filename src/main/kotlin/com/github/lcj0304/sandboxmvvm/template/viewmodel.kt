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
    isListViewModel: Boolean = false
): String {
    var listField = ""
    if (isListViewModel) {
        listField = """val listLayout = ${modelName.getListLayoutName()}()
            val listModel = ${modelName.getListModelName()}(context)
        """.trimMargin()
    }


    return """
package $packageName
       
import android.app.Application        
import com.sandboxol.common.base.app.mvvm.BaseModel
import com.sandboxol.common.base.app.mvvm.BaseViewModel

        
${getFileComments(desc)}   
class ${modelName}VM(context: Application):BaseViewModel<BaseModel>(context) {
    val uc = UIObservable()
    $listField
    init {
        initMessenger()
    }
    
    override fun initMessenger() {
    
    }
    
    
    
    class UIObservable {
    
    }
}
""".trimIndent()
}
