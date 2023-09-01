package com.github.lcj0304.sandboxmvvm.provider

import com.android.tools.idea.wizard.template.*
import com.github.lcj0304.sandboxmvvm.template.getModulePackageName



val isListParameter = booleanParameter{
    name = "是否包含列表"
    default = false
    help = "是否生成 recycler view 相关类"
}

val isPageListParameter = booleanParameter{
    name = "列表是否分页列表"
    default = false
    help = "列表使用分页列表"
    visible = {isListParameter.value}
}

val isDiffParameter = booleanParameter{
    name = "列表是否使用DiffUtil"
    default = false
    help = "列表是否使用DiffUtil"
    visible = {isListParameter.value}
}

val descName = stringParameter {
    name = "Desc Name"
    default = "描述信息"
    help = "请输入描述"
    constraints = listOf(Constraint.NONEMPTY)
}

val classPackageName = stringParameter {
    name = "类目录包名"
    default = "目录包名"
    help = "请输入包名"
    constraints = listOf(Constraint.NONEMPTY)
}

val modulePackageName = stringParameter {
    name = "当前模块包名 如：xxx.xxx.xxx 会自动导入xxx.xxx.xxx.R"
    default = ""
    help = "当前模块资源的包名"
    constraints = listOf(Constraint.NONEMPTY)
    suggest = {getModulePackageName(classPackageName.value)}
}

/**
 * 生成模板配置弹窗 UI
 */
val fragmentGenerator
    get() = template {
        name = "Sandbox Fragment"
        description = "自动创建Fragment、ViewModel和xml"
        minApi = 14
        category = Category.Fragment
        formFactor = FormFactor.Mobile
        screens = listOf(
            WizardUiContext.ActivityGallery,
            WizardUiContext.MenuEntry,
        )

        val modelName = stringParameter {
            name = "类名前缀。如输入XxxZzz 将会生成 XxxZzzFragment XxxZzzViewModel"
            default = "XxxZzz"
            help = "请输入Model的名字，将会生成 XxxZzzFragment XxxZzzViewModel"
            constraints = listOf(Constraint.NONEMPTY)
        }

        val layoutName = stringParameter {
            name = "Fragment 布局文件名"
            default = "fragment_xxx_zzz"
            help = "请输入布局的名字"
            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
            suggest = { "fragment_${camelCaseToUnderlines(modelName.value)}" }
        }




//        val entityName = stringParameter {
//            name = "列表实体类名"
//            default = "XxxZzz"
//            help = "请输入列表实体类名，包含列表时有效"
//            constraints = listOf(Constraint.NONEMPTY)
//            visible = {isListParameter.value}
//        }


        widgets(
            TextFieldWidget(modelName),
            TextFieldWidget(layoutName),
            TextFieldWidget(descName),
            PackageNameWidget(classPackageName),
            TextFieldWidget(modulePackageName),
            CheckBoxWidget(isListParameter),
            CheckBoxWidget(isPageListParameter),
            CheckBoxWidget(isDiffParameter),
//            TextFieldWidget(entityName),
        )

        recipe = {
            simpleFragmentRecipe(
                moduleData = it as ModuleTemplateData,
                modulePackageName = modulePackageName.value,
                packageName = classPackageName.value,
                modelName = modelName.value,
                layoutName = layoutName.value,
                desc = descName.value,
                isList = isListParameter.value,
                isDiff = isDiffParameter.value,
                isPageList = isPageListParameter.value,
//                entityName = entityName.value
            )
        }
    }

val activityGenerator
    get() = template {
        name = "Sandbox Activity"
        description = "自动创建Activity、ViewModel和xml"
        minApi = 14
        category = Category.Activity
        formFactor = FormFactor.Mobile
        screens = listOf(
            WizardUiContext.ActivityGallery,
            WizardUiContext.MenuEntry,
        )

        val modelName = stringParameter {
            name = "类名前缀。如输入XxxZzz 将会生成 XxxZzzActivity XxxZzzViewModel"
            default = "XxxZzz"
            help = "请输入Model的名字"
            constraints = listOf(Constraint.NONEMPTY)
        }

        val layoutName = stringParameter {
            name = "layout name"
            default = "activity_xxx"
            help = "请输入布局的名字"
            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
            suggest = { "activity_${camelCaseToUnderlines(modelName.value)}" }
        }



//        val entityName = stringParameter {
//            name = "列表实体类名"
//            default = ""
//            help = "请输入列表实体类名，包含列表时有效"
//            constraints = listOf(Constraint.NONEMPTY)
//        }

        widgets(
            TextFieldWidget(modelName),
            TextFieldWidget(layoutName),
            TextFieldWidget(descName),
            PackageNameWidget(classPackageName),
            TextFieldWidget(modulePackageName),
            CheckBoxWidget(isListParameter),
            CheckBoxWidget(isPageListParameter),
            CheckBoxWidget(isDiffParameter),
//            TextFieldWidget(entityName),
        )

        recipe = {
            simpleActivityRecipe(
                moduleData = it as ModuleTemplateData,
                modulePackageName = modulePackageName.value,
                packageName = classPackageName.value,
                modelName = modelName.value,
                layoutName = layoutName.value,
                desc = descName.value,
                isList = isListParameter.value,
                isDiff = isDiffParameter.value,
                isPageList = isPageListParameter.value,
//                entityName = entityName.value
            )
        }
    }