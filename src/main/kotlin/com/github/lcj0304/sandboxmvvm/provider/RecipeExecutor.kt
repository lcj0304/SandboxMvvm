package com.github.lcj0304.sandboxmvvm.provider

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.impl.activities.common.addAllKotlinDependencies
import com.github.lcj0304.sandboxmvvm.listeners.MyProjectManagerListener.Companion.projectInstance
import com.github.lcj0304.sandboxmvvm.template.*
import java.io.File


fun RecipeExecutor.simpleFragmentRecipe(
    moduleData: ModuleTemplateData,
    modulePackageName: String,
    packageName: String,
    modelName: String,
    layoutName: String,
    desc: String,
    isList:Boolean = false,
    isDiff:Boolean = false,
    isPageList:Boolean = false,
    entityName:String = "Any"
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
            isDiff,
        ), File(srcPath, "${modelName}VM.kt")
    )

    // 保存xml 布局文件
    save(
        layoutTemplate(packageName, modelName, isList, isDiff, isPageList),
        File(File(resPath, "layout"), "${layoutName}.xml")
    )


    if (isList) {
        val layoutFolder = File(resPath, "layout")
        save(listLayoutTemplate(isDiff, isPageList), File(layoutFolder, "${modelName.getListLayoutXmlName()}.xml"))
        save(listItemLayoutTemplate(packageName, modelName), File(layoutFolder, "${modelName.getListLayoutItemXmlName()}.xml"))
        save(listFileStr(modulePackageName, packageName, modelName, isPageList,"${packageName}.entity", entityName), File(srcPath, "${modelName.getListFileName()}.kt"))
    }
}

fun RecipeExecutor.simpleActivityRecipe(
    moduleData: ModuleTemplateData,
    modulePackageName: String,
    packageName: String,
    modelName: String,
    layoutName: String,
    desc: String,
    isList:Boolean = false,
    isDiff:Boolean = false,
    isPageList:Boolean = false,
    entityName:String = "Any"
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

    // 保存模板文件
    save(
        viewModelTemplate(
            packageName,
            modelName,
            desc,
            isList,
            isDiff
        ), File(srcPath, "${modelName}VM.kt")
    )

    // 保存xml 布局文件
    save(
        layoutTemplate(packageName, modelName, isList, isDiff, isPageList),
        File(File(resPath, "layout"), "${layoutName}.xml")
    )

    if (isList) {
        val layoutFolder = File(resPath, "layout")
        save(listLayoutTemplate(isDiff, isPageList), File(layoutFolder, "${modelName.getListLayoutXmlName()}.xml"))
        save(listItemLayoutTemplate(packageName, modelName), File(layoutFolder, "${modelName.getListLayoutItemXmlName()}.xml"))
        save(listFileStr(modulePackageName, packageName, modelName, isPageList,"${packageName}.entity", entityName), File(srcPath, "${modelName.getListFileName()}.kt"))
    }
}