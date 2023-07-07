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
    isBedWar:Boolean = false
): String {

    val bindingNameClass = "${underscoreToCamelCase(layoutName)}Binding"
    var baseModel = ""
    var getViewModelName = "getVM"
    return """
package $packageName      

import androidx.lifecycle.ViewModelProvider
import com.sandboxol.common.base.app.mvvm.MvvmBaseActivity
import ${modulePackageName}.R
import ${modulePackageName}.databinding.${bindingNameClass}


${getFileComments(desc)}
class ${modelName}Activity:MvvmBaseActivity<${modelName}VM, ${bindingNameClass}>() {
    override val layoutId:Int
        get() = R.layout.${layoutName}

    override fun ${getViewModelName}():${modelName}VM {
        return ViewModelProvider.AndroidViewModelFactory(application)
            .create(${modelName}VM::class.java)
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


