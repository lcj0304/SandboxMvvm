package com.github.lcj0304.sandboxmvvm.provider

import com.android.tools.idea.wizard.template.*
import com.github.lcj0304.sandboxmvvm.template.getModulePackageName

val isBedWarParameter = booleanParameter{
    name = "是否是起床分包项目"
    default = false
    help = "平台和起床分包的getViewModel 方法名不一样"
}


val isListParameter = booleanParameter{
    name = "是否包含列表"
    default = false
    help = "是否生成 recycler view 相关类"
}

val descName = stringParameter {
    name = "Desc Name"
    default = "描述信息"
    help = "请输入描述"
    constraints = listOf(Constraint.NONEMPTY)
}

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
            help = "请输入Model的名字，将会生成 XxxZzzFragment XxxZzzViewModel fragment_xxx_zzz.xml"
            constraints = listOf(Constraint.NONEMPTY)

        }

        val layoutName = stringParameter {
            name = "layout name"
            default = "fragment_xxx"
            help = "请输入布局的名字"
            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
            suggest = { "fragment_${camelCaseToUnderlines(modelName.value)}" }
        }



        val packageName = stringParameter {
            name = "com.sandboxol.xxx.xxx"
            default = "包名"
            help = "请输入包名"
            constraints = listOf(Constraint.NONEMPTY)
        }
        val entityName = stringParameter {
            name = "列表实体类名"
            default = ""
            help = "请输入列表实体类名，包含列表时有效"
            constraints = listOf(Constraint.NONEMPTY)
        }


        widgets(
            TextFieldWidget(modelName),
            TextFieldWidget(layoutName),
            TextFieldWidget(descName),
            PackageNameWidget(packageName),
            CheckBoxWidget(isListParameter),
            CheckBoxWidget(isBedWarParameter),
            TextFieldWidget(entityName),
        )

        recipe = {
            simpleFragmentRecipe(
                moduleData = it as ModuleTemplateData,
                modulePackageName = getModulePackageName(packageName.value),
                packageName = packageName.value,
                modelName = modelName.value,
                layoutName = layoutName.value,
                desc = descName.value,
                isList = isListParameter.value,
                isBedWar = isBedWarParameter.value,
                entityName = entityName.value
            )
        }
    }

val activityGenerator
    get() = template {
        name = "Sandbox Mvvm activity"
        description = "自动生成MvvmActivity、ViewModel和xml"
        minApi = 14
        category = Category.Activity
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
            help = "请输入Model的名字，将会生成 XxxZzzActivity XxxZzzViewModel activity_xxx_zzz.xml"
            constraints = listOf(Constraint.NONEMPTY)
        }

        val layoutName = stringParameter {
            name = "layout name"
            default = "activity_xxx"
            help = "请输入布局的名字"
            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
            suggest = { "activity_${camelCaseToUnderlines(modelName.value)}" }
        }


        val packageName = stringParameter {
            name = "com.sandboxol.xxx.xxx"
            default = "包名"
            help = "请输入包名"
            constraints = listOf(Constraint.NONEMPTY)
        }
        val entityName = stringParameter {
            name = "列表实体类名"
            default = ""
            help = "请输入列表实体类名，包含列表时有效"
            constraints = listOf(Constraint.NONEMPTY)
        }

        widgets(
            TextFieldWidget(modelName),
            TextFieldWidget(layoutName),
            TextFieldWidget(descName),
            PackageNameWidget(packageName),
            CheckBoxWidget(isListParameter),
            CheckBoxWidget(isBedWarParameter),
            TextFieldWidget(entityName),
        )

        recipe = {
            simpleActivityRecipe(
                moduleData = it as ModuleTemplateData,
                modulePackageName = getModulePackageName(packageName.value),
                packageName = packageName.value,
                modelName = modelName.value,
                layoutName = layoutName.value,
                desc = descName.value,
                isList = isListParameter.value,
                isBedWar = isBedWarParameter.value,
                entityName = entityName.value
            )
        }
    }