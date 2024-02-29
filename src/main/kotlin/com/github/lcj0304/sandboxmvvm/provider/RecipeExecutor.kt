package com.github.lcj0304.sandboxmvvm.provider

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.impl.activities.common.addAllKotlinDependencies
import com.github.lcj0304.sandboxmvvm.listeners.MyProjectManagerListener.Companion.projectInstance
import com.github.lcj0304.sandboxmvvm.template.ListInfo
import com.github.lcj0304.sandboxmvvm.template.activityTemplate
import com.github.lcj0304.sandboxmvvm.template.fragmentTemplate
import com.github.lcj0304.sandboxmvvm.template.getListFileName
import com.github.lcj0304.sandboxmvvm.template.layoutTemplate
import com.github.lcj0304.sandboxmvvm.template.listFileStr
import com.github.lcj0304.sandboxmvvm.template.listItemLayoutTemplate
import com.github.lcj0304.sandboxmvvm.template.listLayoutTemplate
import com.github.lcj0304.sandboxmvvm.template.viewModelTemplate
import java.io.File


fun RecipeExecutor.simpleFragmentRecipe(
    moduleData: ModuleTemplateData,
    modulePackageName: String,
    packageName: String,
    modelName: String,
    layoutName: String,
    desc: String,
    isList: Boolean = false,
    listInfo: ListInfo = ListInfo(),
) {
    val (projectData) = moduleData
    val project = projectInstance ?: return
    addAllKotlinDependencies(moduleData)
    val srcPath = moduleData.srcDir.absolutePath
    val resPath = moduleData.resDir.absolutePath
    // 保存Fragment文件
    save(
        fragmentTemplate(
            modulePackageName,
            packageName,
            modelName,
            layoutName,
            desc,
        ), File(srcPath, "${modelName}Fragment.kt")
    )

    // 保存模板文件
    save(
        viewModelTemplate(
            packageName,
            modelName,
            desc,
            isList,
            listInfo.isDiff,
        ), File(srcPath, "${modelName}VM.kt")
    )

    // 保存xml 布局文件
    save(
        layoutTemplate(packageName, modelName, isList, listInfo.isDiff, listInfo.isPageList),
        File(File(resPath, "layout"), "${layoutName}.xml")
    )


    // 列表
    if (isList) {
        saveListFile(
            moduleData,
            modulePackageName,
            packageName,
            modelName,
            listInfo
        )
    }
}

fun RecipeExecutor.simpleActivityRecipe(
    moduleData: ModuleTemplateData,
    modulePackageName: String,
    packageName: String,
    modelName: String,
    layoutName: String,
    desc: String,
    isList: Boolean = false,
    listInfo: ListInfo = ListInfo(),
) {
    val (projectData) = moduleData
    val project = projectInstance ?: return
    addAllKotlinDependencies(moduleData)
    val srcPath = moduleData.srcDir.absolutePath
    val resPath = moduleData.resDir.absolutePath
    // 保存Activity文件
    save(
        activityTemplate(
            modulePackageName,
            packageName,
            modelName,
            layoutName,
            desc,
        ), File(srcPath, "${modelName}Activity.kt")
    )

    // 保存View Model文件
    save(
        viewModelTemplate(
            packageName,
            modelName,
            desc,
            isList,
            listInfo.isDiff
        ), File(srcPath, "${modelName}VM.kt")
    )

    // 保存xml 布局文件
    save(
        layoutTemplate(packageName, modelName, isList, listInfo.isDiff, listInfo.isPageList),
        File(File(resPath, "layout"), "${layoutName}.xml")
    )

    if (isList) {
        saveListFile(
            moduleData,
            modulePackageName,
            packageName,
            modelName,
            listInfo
        )
    }
}

fun RecipeExecutor.saveListFile(
    moduleData: ModuleTemplateData,
    modulePackageName: String,
    packageName: String,
    modelName: String,
    listInfo: ListInfo
) {
    val (projectData) = moduleData
    val project = projectInstance ?: return
    addAllKotlinDependencies(moduleData)
    val srcPath = moduleData.srcDir.absolutePath
    val resPath = moduleData.resDir.absolutePath
    val layoutFolder = File(resPath, "layout")

    // list layout xml
    save(
        listLayoutTemplate(listInfo.isDiff, listInfo.isPageList),
        File(layoutFolder, "${listInfo.listLayoutXmlName}.xml")
    )

    // list item layout xml
    save(
        listItemLayoutTemplate(packageName, modelName),
        File(layoutFolder, "${listInfo.itemLayoutXmlName}.xml")
    )

    // list layout list model  item view model
    save(
        listFileStr(
            modulePackageName,
            packageName,
            modelName,
            listInfo,
            "${packageName}.entity",
        ), File(srcPath, "${modelName.getListFileName()}.kt")
    )
}