package com.github.lcj0304.sandboxmvvm.template

import com.android.tools.idea.wizard.template.underscoreToCamelCase
import com.sun.tools.javac.code.Type.moreInfo


/**
 * @description ：
 * @author :liucj
 * @date : 2023/4/28 10:58
 */
fun activityTemplate(
    modulePackageName: String,
    packageName: String,
    modelName: String,
    layoutName: String,
    desc: String = "TODO:",
    moreInfo: MoreInfo = MoreInfo()
): String {

    val bindingNameClass = "${underscoreToCamelCase(layoutName)}Binding"

    val activityFullName = if (moreInfo.isEventPage) {
        "com.sandboxol.center.view.activity.event.BaseEventActivity"
    } else {
        "com.sandboxol.common.base.app.mvvm.MvvmBaseActivity"
    }

    val activityName = if (moreInfo.isEventPage) {
        "BaseEventActivity"
    } else {
        "MvvmBaseActivity"
    }




    return """
package $packageName      

import $activityFullName
import ${modulePackageName}.R
import ${modulePackageName}.databinding.${bindingNameClass}
import com.sandboxol.center.extension.getVMFromProvider

${getFileComments(desc)}
class ${modelName}Activity:${activityName}<${modelName}VM, ${bindingNameClass}>() {
    override val layoutId:Int
        get() = R.layout.${layoutName}

    override fun settingViewModel():${modelName}VM {
        return getVMFromProvider(${modelName}VM::class.java)
    }
    
    override fun bindViewModel(binding:${bindingNameClass}?, viewModel:${modelName}VM?) {
        binding?.viewModel = viewModel
    }  

    override fun initViewObservable() {
        viewModel?.uc?.apply {
            
        }
    }
    
}
""".trimIndent()
}


