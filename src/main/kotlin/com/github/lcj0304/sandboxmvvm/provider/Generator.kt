package com.github.lcj0304.sandboxmvvm.provider

import com.android.tools.idea.wizard.template.*
import com.github.lcj0304.sandboxmvvm.template.MoreInfo
import com.github.lcj0304.sandboxmvvm.template.getListLayoutItemXmlName
import com.github.lcj0304.sandboxmvvm.template.getListLayoutXmlName
import com.github.lcj0304.sandboxmvvm.template.getModulePackageName



val isListParameter = booleanParameter{
    name = "是否包含列表"
    default = false
    help = "是否生成 recycler view 相关类"
}

val isPageListParameter = booleanParameter{
    name = "列表是否分页"
    default = false
    help = "列表使用分页"
    visible = {isListParameter.value}
}

val isDiffParameter = booleanParameter{
    name = "列表是否使用DiffUtil"
    default = false
    help = "列表是否使用DiffUtil"
    visible = {isListParameter.value}
}

val isEventParameter = booleanParameter{
    name = "是否为活动页面"
    default = false
    help = "活动页面"
}

val isTaskList = booleanParameter {
    name = "是否为任务列表"
    default = false
    help = "活动任务列表，将会生成任务列表相关代码"
    visible = {isListParameter.value && isEventParameter.value}
}

val descName = stringParameter {
    name = "类的描述信息"
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
        name = "Sandbox MvvmFragment"
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

        val itemLayoutXmlFileName  = stringParameter {
            name = "item布局文件名"
            default = "xxx_zzz_list_item_view"
            help = "列表 item 布局 文件名"
            visible = {isListParameter.value}
            constraints = listOf(Constraint.NONEMPTY)
            suggest = { modelName.value.getListLayoutItemXmlName() }
        }

        val listLayoutXmlFileName  = stringParameter {
            name = "list layout布局文件名"
            default = "xxx_zzz_layout"
            help = "列表布局文件名"
            visible = {isListParameter.value}
            constraints = listOf(Constraint.NONEMPTY)
            suggest = { modelName.value.getListLayoutXmlName() }
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
            CheckBoxWidget(isEventParameter),
            CheckBoxWidget(isListParameter),
            CheckBoxWidget(isPageListParameter),
            CheckBoxWidget(isDiffParameter),
            CheckBoxWidget(isTaskList),
            TextFieldWidget(listLayoutXmlFileName),
            TextFieldWidget(itemLayoutXmlFileName),
//            TextFieldWidget(entityName),
        )


        recipe = {
            val moreInfo = MoreInfo()
            moreInfo.isDiff = isDiffParameter.value
            moreInfo.isPageList = isPageListParameter.value
            moreInfo.listLayoutXmlName = listLayoutXmlFileName.value.trim()
            moreInfo.itemLayoutXmlName = itemLayoutXmlFileName.value.trim()
            moreInfo.isEventPage = isEventParameter.value
            moreInfo.isTaskList = isTaskList.value
            simpleFragmentRecipe(
                moduleData = it as ModuleTemplateData,
                modulePackageName = modulePackageName.value.trim(),
                packageName = classPackageName.value.trim(),
                modelName = modelName.value.trim(),
                layoutName = layoutName.value.trim(),
                desc = descName.value.trim(),
                isList = isListParameter.value,
                moreInfo
            )
        }
    }

val activityGenerator
    get() = template {
        name = "Sandbox MvvmActivity"
        description = "自动创建Activity、ViewModel和xml"
        minApi = 14
        category = Category.Activity
        formFactor = FormFactor.Mobile
        screens = listOf(
            WizardUiContext.ActivityGallery,
            WizardUiContext.MenuEntry,
        )

        val modelName = stringParameter {
            name = "Activity类名前缀。如输入XxxZzz 将会生成 XxxZzzActivity XxxZzzViewModel"
            default = "XxxZzz"
            help = "请输入Model的名字"
            constraints = listOf(Constraint.NONEMPTY)
        }

        val layoutName = stringParameter {
            name = "Activity 布局文件名"
            default = "activity_xxx"
            help = "请输入布局的名字"
            constraints = listOf(Constraint.LAYOUT, Constraint.UNIQUE, Constraint.NONEMPTY)
            suggest = { "activity_${camelCaseToUnderlines(modelName.value)}" }
        }

        val itemLayoutXmlFileName  = stringParameter {
            name = "item布局文件名"
            default = "xxx_zzz_list_item_view"
            help = "列表 item 布局 文件名"
            visible = {isListParameter.value}
            constraints = listOf(Constraint.NONEMPTY)
            suggest = { modelName.value.getListLayoutItemXmlName() }
        }

        val listLayoutXmlFileName  = stringParameter {
            name = "list layout布局文件名"
            default = "xxx_zzz_layout"
            help = "列表布局文件名"
            visible = {isListParameter.value}
            constraints = listOf(Constraint.NONEMPTY)
            suggest = { modelName.value.getListLayoutXmlName() }
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
            CheckBoxWidget(isEventParameter),
            CheckBoxWidget(isListParameter),
            CheckBoxWidget(isPageListParameter),
            CheckBoxWidget(isDiffParameter),
            CheckBoxWidget(isTaskList),
            TextFieldWidget(listLayoutXmlFileName),
            TextFieldWidget(itemLayoutXmlFileName),
//            TextFieldWidget(entityName),
        )

        recipe = {
            val moreInfo = MoreInfo()
            moreInfo.isDiff = isDiffParameter.value
            moreInfo.isPageList = isPageListParameter.value
            moreInfo.listLayoutXmlName = listLayoutXmlFileName.value.trim()
            moreInfo.itemLayoutXmlName = itemLayoutXmlFileName.value.trim()
            moreInfo.isEventPage = isEventParameter.value
            moreInfo.isTaskList = isTaskList.value
            simpleActivityRecipe(
                moduleData = it as ModuleTemplateData,
                modulePackageName = modulePackageName.value.trim(),
                packageName = classPackageName.value.trim(),
                modelName = modelName.value.trim(),
                layoutName = layoutName.value.trim(),
                desc = descName.value.trim(),
                isList = isListParameter.value,
                moreInfo
//                entityName = entityName.value
            )
        }
    }


val listViewGenerator
    get() = template {
        name = "Sandbox list view"
        description = "自动创建list layout、listModel itemViewModel和xml"
        minApi = 14
        category = Category.Fragment
        formFactor = FormFactor.Mobile
        screens = listOf(
            WizardUiContext.ActivityGallery,
            WizardUiContext.MenuEntry,
        )

        val modelName = stringParameter {
            name = "生成的类名前缀。如输入XxxZzz 将会生成 XxxZzzListLayout XxxZzzListModel  XxxZzzItemViewModel"
            default = "XxxZzz"
            help = "请输入Model的名字，将会生成 XxxZzzListLayout XxxZzzListModel"
            constraints = listOf(Constraint.NONEMPTY)
        }

        val itemLayoutXmlFileName  = stringParameter {
            name = "item布局文件名"
            default = "xxx_zzz_list_item_view"
            help = "列表 item 布局 文件名"
            visible = {isListParameter.value}
            constraints = listOf(Constraint.NONEMPTY)
            suggest = { modelName.value.getListLayoutItemXmlName() }
        }

        val listLayoutXmlFileName  = stringParameter {
            name = "list layout布局文件名"
            default = "xxx_zzz_layout"
            help = "列表布局文件名"
            visible = {isListParameter.value}
            constraints = listOf(Constraint.NONEMPTY)
            suggest = { modelName.value.getListLayoutXmlName() }
        }

        widgets(
            TextFieldWidget(modelName),
            PackageNameWidget(classPackageName),
            TextFieldWidget(modulePackageName),
            CheckBoxWidget(isPageListParameter),
            CheckBoxWidget(isDiffParameter),
            CheckBoxWidget(isTaskList),
            TextFieldWidget(listLayoutXmlFileName),
            TextFieldWidget(itemLayoutXmlFileName),
        )

        recipe = {
            val moreInfo = MoreInfo()
            moreInfo.isDiff = isDiffParameter.value
            moreInfo.isPageList = isPageListParameter.value
            moreInfo.isTaskList = isTaskList.value
            moreInfo.listLayoutXmlName = listLayoutXmlFileName.value.trim()
            moreInfo.itemLayoutXmlName = itemLayoutXmlFileName.value.trim()
            saveListFile(
                it as ModuleTemplateData,
                modulePackageName.value.trim(),
                classPackageName.value.trim(),
                modelName.value.trim(),
                moreInfo
            )
        }
    }