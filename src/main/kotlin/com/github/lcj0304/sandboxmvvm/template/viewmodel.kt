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
    var listLayoutField = ""
    var listModelField = ""
    var diffImport = ""
    var diff = ""
    var baseTaskImport = ""
    var onButtonClickFunction = ""
    if (isListViewModel) {

        listLayoutField = """val listLayout = ${modelName.getListLayoutName()}()""".trimMargin()
        listModelField = """val listModel = ${modelName.getListModelName()}(context)""".trimMargin()
        if (isDiffList) {
            diffImport = "import androidx.recyclerview.widget.DiffUtil"
            diff = getDiff(entityName)
        }

        // 如果包含任务列表，这里做特殊处理，加入BaseTask 相关、点击相关事件 diff 相关事件
        if (moreInfo.isTaskList) {
            baseTaskImport = """
                import com.sandboxol.center.entity.task.BaseTask
                import com.sandboxol.center.entity.task.BaseTaskDiffCallback
                import com.sandboxol.center.entity.task.TaskStatus
            """.trimIndent()
            onButtonClickFunction = buttonClickFun
            diff = """val diffItemCallback = BaseTaskDiffCallback()"""
            listModelField = """val listModel = ${modelName.getListModelName()}(context, this::onButtonClick) {
                showPreviewRewardDialog(it)
            }
            """.trimIndent()
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
$baseTaskImport
        
${getFileComments(desc)}   
class ${modelName}VM(context: Application, bundle:Bundle?):${viewModelName}<BaseModel>(context, bundle) {
    val uc = UIObservable()
    $listLayoutField
    $listModelField
    init {
        initMessenger()
    }
    
    override fun initMessenger() {
    
    }
    
    $onButtonClickFunction
    
    class UIObservable {
    
    }
    
$diff    
}
""".trimIndent()
}



val buttonClickFun = """
    fun onButtonClick(task: BaseTask) {
        when (task.status) {
            TaskStatus.TO_BE_COMPLETED -> {
                if (task.isCanGotoTaskPage()) {
                    gotoPage(task.toGotoParams())
                }
            }

            TaskStatus.COMPLETED -> {
                receiveTaskReward(task)
            }

            TaskStatus.RECEIVED -> {

            }
        }
    }
    
    fun receiveTaskReward(task: BaseTask) {
        // todo: call api to receive task reward 
    }
""".trimIndent()