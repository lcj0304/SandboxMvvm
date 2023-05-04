package com.github.lcj0304.sandboxmvvm.template

import com.android.tools.idea.wizard.template.underscoreToCamelCase


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
): String {

    val bindingNameClass = "${underscoreToCamelCase(layoutName)}Binding"
    var baseModel = ""

    return """
package $packageName      
import ${packageName}.${modelName}VM
import com.sandboxol.center.extension.getAndroidVM
import com.sandboxol.common.base.app.mvvm.MvvmBaseActivity
import ${modulePackageName}.R
import ${modulePackageName}.databinding.${bindingNameClass}


${getFileComments(desc)}
class ${modelName}Activity:MvvmBaseActivity<${modelName}VM, ${bindingNameClass}>() {
    override val layoutId:Int
        get() = R.layout.${layoutName}

    override fun getVM():${modelName}VM {
        return getAndroidVM(${modelName}VM::class.java)
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


