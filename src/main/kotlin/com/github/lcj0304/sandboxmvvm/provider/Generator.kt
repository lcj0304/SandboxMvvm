package com.github.lcj0304.sandboxmvvm.provider

import com.android.tools.idea.wizard.template.*
import com.github.lcj0304.sandboxmvvm.template.getModulePackageName

/**
 * 生成模板配置弹窗 UI
 */
val fragmentGenerator
    get() = template {
        name = "Sandbox Mvvm Fragment"
        description = "自动生成MvvmFragment、ViewModel和xml"
        minApi = 14
        category = Category.Fragment
        formFactor = FormFactor.Mobile
        screens = listOf(
            WizardUiContext.ActivityGallery,
            WizardUiContext.MenuEntry,
            WizardUiContext.NewProject,
            WizardUiContext.NewModule
        )


        val modelName = stringParameter {
            name = "Model Name"
            default = "XxxZzz"
            help = "请输入Model的名字，将会生成 XxxZzzFragment XxxZzzViewModel fragment_xxx_zzz"
            constraints = listOf(Constraint.NONEMPTY)

        }

        val layoutName = stringParameter {
            name = "layout name"
            default = "fragment_xxx"
            help = "请输入布局的名字"
            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
            suggest = { "fragment_${camelCaseToUnderlines(modelName.value)}" }
        }


        val descName = stringParameter {
            name = "Desc Name"
            default = "描述信息"
            help = "请输入描述"
            constraints = listOf(Constraint.NONEMPTY)

        }

        val packageName = stringParameter {
            name = "com.sandboxol.xxx.xxx"
            default = "包名"
            help = "请输入包名"
            constraints = listOf(Constraint.NONEMPTY)
        }




        widgets(
            TextFieldWidget(modelName),
            TextFieldWidget(layoutName),
            TextFieldWidget(descName),
            PackageNameWidget(packageName),
        )

        recipe = {
            simpleFragmentRecipe(
                moduleData = it as ModuleTemplateData,
                modulePackageName = getModulePackageName(packageName.value),
                packageName = packageName.value,
                modelName = modelName.value,
                layoutName = layoutName.value,
                desc = descName.value,
            )
        }
    }

