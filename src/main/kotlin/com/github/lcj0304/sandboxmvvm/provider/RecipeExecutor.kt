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
    isBedWar:Boolean = false,
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
            isBedWar
        ), File(srcPath, "${modelName}Fragment.kt")
    )

    // 保存模板文件
    save(
        viewModelTemplate(
            packageName,
            modelName,
            desc,
            isList
        ), File(srcPath, "${modelName}VM.kt")
    )

    // 保存xml 布局文件
    save(
        layoutTemplate(packageName, modelName, isList),
        File(File(resPath, "layout"), "${layoutName}.xml")
    )


    if (isList) {
        val layoutFolder = File(resPath, "layout")
        save(listLayoutTemplate(), File(layoutFolder, "${modelName.getListLayoutXmlName()}.xml"))
        save(listItemLayoutTemplate(packageName, modelName), File(layoutFolder, "${modelName.getListLayoutItemXmlName()}.xml"))
        save(listFileStr(modulePackageName, packageName, modelName, "${packageName}.entity", "Entity"), File(srcPath, "${modelName.getListFileName()}.kt"))

//        save(listModelTemplate(modulePackageName, packageName, modelName, desc), File(srcPath, "${modelName.getListModelName()}.kt"))
//        save(listLayoutTemplate(modulePackageName, packageName, modelName, desc), File(srcPath, "${modelName.getListLayoutName()}.kt"))
//        save(listItemTemplate(modulePackageName, packageName, modelName, desc), File(srcPath, "${modelName.getListItemViewModelName()}.kt"))
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
    isBedWar: Boolean = false
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
            isBedWar
        ), File(srcPath, "${modelName}Activity.kt")
    )

    // 保存模板文件
    save(
        viewModelTemplate(
            packageName,
            modelName,
            desc,
            isList
        ), File(srcPath, "${modelName}VM.kt")
    )

    // 保存xml 布局文件
    save(
        layoutTemplate(packageName, modelName, isList),
        File(File(resPath, "layout"), "${layoutName}.xml")
    )

    if (isList) {
        val layoutFolder = File(resPath, "layout")
        save(listLayoutTemplate(), File(layoutFolder, "${modelName.getListLayoutXmlName()}.xml"))
        save(listItemLayoutTemplate(packageName, modelName), File(layoutFolder, "${modelName.getListLayoutItemXmlName()}.xml"))
        save(listModelTemplate(modulePackageName, packageName, modelName, desc), File(srcPath, "${modelName.getListModelName()}.kt"))
        save(listLayoutTemplate(modulePackageName, packageName, modelName, desc), File(srcPath, "${modelName.getListLayoutName()}.kt"))
        save(listItemTemplate(modulePackageName, packageName, modelName, desc), File(srcPath, "${modelName.getListItemViewModelName()}.kt"))
    }
}