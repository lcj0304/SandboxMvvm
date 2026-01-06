package com.github.lcj0304.sandboxmvvm.provider

import com.android.tools.idea.wizard.template.Category
import com.android.tools.idea.wizard.template.Constraint
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.TextFieldWidget
import com.android.tools.idea.wizard.template.WizardUiContext
import com.android.tools.idea.wizard.template.stringParameter
import com.android.tools.idea.wizard.template.template
import com.sun.tools.javac.code.Type.moreInfo
import java.util.Locale


/**
 * 生成模板配置弹窗 UI
 */
val moduleGenerator
    get() = template {
        name = "Sandbox module"
        description = "自动创建 module 模板代码"
        minApi = 14
        category = Category.Other
        formFactor = FormFactor.Mobile
        screens = listOf(
            WizardUiContext.ActivityGallery,
            WizardUiContext.MenuEntry,
        )

        val moduleName = stringParameter {
            name = "命名方式驼峰 如：RandomDraw. 模块名和文件夹会是randomdraw  模块相关类名会是RandomDrawXxx"
            default = "XxxYyy"
            help = ""
            constraints = listOf(Constraint.NONEMPTY)
        }



        widgets(
            TextFieldWidget(moduleName),
        )


        recipe = {
            createModule(
                it as ModuleTemplateData,
                moduleName = moduleName.value,
            )
        }
    }