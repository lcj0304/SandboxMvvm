package com.github.lcj0304.sandboxmvvm.template

import com.android.tools.idea.wizard.template.underscoreToCamelCase


/**
 * @description ：
 * @author :liucj
 * @date : 2023/4/28 10:58
 */
fun fragmentTemplate(
    modulePackageName: String,
    packageName: String,
    modelName: String,
    layoutName: String,
    desc: String = "TODO:",
    moreInfo: MoreInfo = MoreInfo()
): String {

    val bindingNameClass = "${underscoreToCamelCase(layoutName)}Binding"

    val fragmentFullName = if (moreInfo.isEventPage) {
        "com.sandboxol.center.view.fragment.event.BaseEventFragment"
    } else {
        "com.sandboxol.common.base.app.mvvm.MvvmBaseFragment"
    }

    val fragmentName = if (moreInfo.isEventPage) {
        "BaseEventFragment"
    } else {
        "MvvmBaseFragment"
    }
    val callSuperInitViewObservable = if (moreInfo.isEventPage) {
        "super.initViewObservable()"
    } else {
        ""
    }

    return """
package $packageName
      
import ${fragmentFullName}
import ${modulePackageName}.R
import ${modulePackageName}.databinding.${bindingNameClass}
import com.sandboxol.center.extension.getVMFromProvider

${getFileComments(desc)}
class ${modelName}Fragment:${fragmentName}<${modelName}VM, ${bindingNameClass}>() {
    override val layoutId:Int
        get() = R.layout.${layoutName}

    override fun settingViewModel():${modelName}VM {
        return getVMFromProvider(${modelName}VM::class.java)
    }
    
    override fun bindViewModel(binding:${bindingNameClass}?, viewModel:${modelName}VM?) {
        binding?.viewModel = viewModel
    }  

    override fun initViewObservable() {
        $callSuperInitViewObservable
        initView()
        viewModel?.uc?.apply {
            
        }
    }
    
    private fun initView() {
    }
    
    companion object {
        fun newInstance(): ${modelName}Fragment {
            return ${modelName}Fragment()
        }
    }
}
""".trimIndent()
}


