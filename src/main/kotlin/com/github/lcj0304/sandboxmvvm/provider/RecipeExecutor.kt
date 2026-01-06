package com.github.lcj0304.sandboxmvvm.provider

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.impl.activities.common.addAllKotlinDependencies
import com.github.lcj0304.sandboxmvvm.listeners.MyProjectManagerListener.Companion.projectInstance
import com.github.lcj0304.sandboxmvvm.template.MoreInfo
import com.github.lcj0304.sandboxmvvm.template.activityTemplate
import com.github.lcj0304.sandboxmvvm.template.addModuleAppConstAndAppendInitEntry
import com.github.lcj0304.sandboxmvvm.template.addModuleDependency
import com.github.lcj0304.sandboxmvvm.template.addRouterServiceInnerClass
import com.github.lcj0304.sandboxmvvm.template.fragmentTemplate
import com.github.lcj0304.sandboxmvvm.template.getAppClassContent
import com.github.lcj0304.sandboxmvvm.template.getListFileName
import com.github.lcj0304.sandboxmvvm.template.getManifestContent
import com.github.lcj0304.sandboxmvvm.template.getModuleApiContent
import com.github.lcj0304.sandboxmvvm.template.getModuleApiInterfaceContent
import com.github.lcj0304.sandboxmvvm.template.getModuleManagerContent
import com.github.lcj0304.sandboxmvvm.template.getModuleServiceImplContent
import com.github.lcj0304.sandboxmvvm.template.getModulesGradleContent
import com.github.lcj0304.sandboxmvvm.template.getServiceInterfaceContent
import com.github.lcj0304.sandboxmvvm.template.insertIncludeAfterApp
import com.github.lcj0304.sandboxmvvm.template.layoutTemplate
import com.github.lcj0304.sandboxmvvm.template.listFileStr
import com.github.lcj0304.sandboxmvvm.template.listItemLayoutTemplate
import com.github.lcj0304.sandboxmvvm.template.listLayoutTemplate
import com.github.lcj0304.sandboxmvvm.template.viewModelTemplate
import java.io.File
import java.nio.file.Path
import java.util.Locale

fun RecipeExecutor.simpleFragmentRecipe(
    moduleData: ModuleTemplateData,
    modulePackageName: String,
    packageName: String,
    modelName: String,
    layoutName: String,
    desc: String,
    isList: Boolean = false,
    moreInfo: MoreInfo = MoreInfo(),
) {
    val (projectData) = moduleData
    val project = projectInstance ?: return
    require(false) {
        "project.basePath: is ${project.basePath}"
    }

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
            moreInfo = moreInfo
        ), File(srcPath, "${modelName}Fragment.kt")
    )

    // 保存模板文件
    save(
        viewModelTemplate(
            packageName,
            modelName,
            desc,
            isList,
            moreInfo.isDiff,
            moreInfo = moreInfo
        ), File(srcPath, "${modelName}VM.kt")
    )

    // 保存xml 布局文件
    save(
        layoutTemplate(packageName, modelName, isList, moreInfo.isDiff, moreInfo.isPageList),
        File(File(resPath, "layout"), "${layoutName}.xml")
    )


    // 列表
    if (isList) {
        saveListFile(
            moduleData,
            modulePackageName,
            packageName,
            modelName,
            moreInfo
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
    moreInfo: MoreInfo = MoreInfo(),
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
            moreInfo = moreInfo
        ), File(srcPath, "${modelName}Activity.kt")
    )

    // 保存View Model文件
    save(
        viewModelTemplate(
            packageName,
            modelName,
            desc,
            isList,
            moreInfo.isDiff,
            moreInfo = moreInfo
        ), File(srcPath, "${modelName}VM.kt")
    )

    // 保存xml 布局文件
    save(
        layoutTemplate(packageName, modelName, isList, moreInfo.isDiff, moreInfo.isPageList),
        File(File(resPath, "layout"), "${layoutName}.xml")
    )

    if (isList) {
        saveListFile(
            moduleData,
            modulePackageName,
            packageName,
            modelName,
            moreInfo
        )
    }
}

fun RecipeExecutor.saveListFile(
    moduleData: ModuleTemplateData,
    modulePackageName: String,
    packageName: String,
    modelName: String,
    moreInfo: MoreInfo
) {
    val (projectData) = moduleData
    val project = projectInstance ?: return
    addAllKotlinDependencies(moduleData)
    val srcPath = moduleData.srcDir.absolutePath
    val resPath = moduleData.resDir.absolutePath
    val layoutFolder = File(resPath, "layout")

    // list layout xml
    save(
        listLayoutTemplate(moreInfo.isDiff, moreInfo.isPageList),
        File(layoutFolder, "${moreInfo.listLayoutXmlName}.xml")
    )

    // list item layout xml
    save(
        listItemLayoutTemplate(modulePackageName, packageName, modelName, moreInfo),
        File(layoutFolder, "${moreInfo.itemLayoutXmlName}.xml")
    )

    // list layout list model  item view model
    save(
        listFileStr(
            modulePackageName,
            packageName,
            modelName,
            moreInfo,
            "${packageName}.entity",
        ), File(srcPath, "${modelName.getListFileName()}.kt")
    )
}

/**
 * 创建模块  相关文件
 *
 */
fun RecipeExecutor.createModule(
    moduleData: ModuleTemplateData,
    moduleName: String
) {
    val rootPath = moduleData.rootDir.absolutePath


//    D:\source\android\blockman-go-android-innereditor\Librarys\libBaseRes\src\main\java\com\sandboxol\center\router
//    D:\source\android\blockman-go-android-innereditor\Blockymods\app


    val projectPath =
        rootPath.substringBeforeLast(File.separator).substringBeforeLast(File.separator)
            .plus(File.separator)
    val appParentPath = rootPath.substringBeforeLast(File.separator).plus(File.separator)

    val moduleNameLowercase = moduleName.lowercase(Locale.getDefault())
    val modulePackName = "com.sandboxol.$moduleNameLowercase"
    val packagePath =
        modulePackName.replace(".", File.separator)


    val moduleBasePath = appParentPath + moduleNameLowercase + File.separator
    val moduleSrcPath =
        moduleBasePath + "src${File.separator}main${File.separator}java${File.separator}" + packagePath
    val moduleResPath = moduleBasePath + "src${File.separator}main${File.separator}res"
    val moduleMainPath = moduleBasePath + "src${File.separator}main${File.separator}"


    // 模块基础文件  build.gradle AndroidManifest.xml  proguard-rules.pro
    createModuleBaseFile(moduleBasePath, moduleMainPath, moduleName)

    // 资源目录
    createResDir(moduleResPath)

    // 代码目录
    createSourceFile(moduleSrcPath, moduleName)

    // lib base res 相关文件   manager 和 service 接口
    createLibBaseResFile(projectPath, moduleName)

    // 添加模块路由和App 类信息到 ModuleManager
    addModuleInfoToModuleManager(projectPath, appParentPath, moduleName)
}

fun createResDir(moduleResPath: String) {
    val moduleResFile = File(moduleResPath)
    if (!moduleResFile.exists()) {
        moduleResFile.mkdirs()
    }

    val list = listOf("drawable", "layout", "drawable-xxhdpi", "values", "mipmap-xxxhdpi")

    list.forEach {
        val file = File(moduleResPath, it)
        if (!file.exists()) {
            file.mkdirs()
        }
    }
}

fun RecipeExecutor.createModuleBaseFile(
    moduleBasePath: String,
    moduleMainPath: String,
    moduleName: String
) {
    save("", File(moduleBasePath, "consumer-rules.pro"))
    save("", File(moduleBasePath, "proguard-rules.pro"))
    save(getModulesGradleContent(moduleName), File(moduleBasePath, "build.gradle"))
    save("""/build""", File(moduleBasePath, ".gitignore"))
    save(getManifestContent(moduleName), File(moduleMainPath, "AndroidManifest.xml"))
}


fun RecipeExecutor.createSourceFile(moduleSrcPath: String, moduleName: String) {
    createSourceDir(moduleSrcPath)

    // save app source file
    save(
        getAppClassContent(moduleName),
        File(moduleSrcPath, "${moduleName}App.kt")
    )

    // service source file
    save(
        getModuleServiceImplContent(moduleName),
        File(moduleSrcPath.plus("/service"), "${moduleName}Service.kt")
    )

    // web/ api source file
    save(
        getModuleApiInterfaceContent(moduleName),
        File(moduleSrcPath.plus("/web"), "I${moduleName}Api.kt")
    )
    save(
        getModuleApiContent(moduleName),
        File(moduleSrcPath.plus("/web"), "${moduleName}Api.kt")
    )
}


fun createSourceDir(moduleSrcPath: String) {
    val moduleSrcFile = File(moduleSrcPath)
    if (!moduleSrcFile.exists()) {
        moduleSrcFile.mkdirs()
    }

    val list = listOf("/entity", "/service", "/view", "view/activity", "view/fragment", "/web")

    list.forEach {
        val file = File(moduleSrcFile, it)
        if (!file.exists()) {
            file.mkdirs()
        }
    }
}


fun RecipeExecutor.createLibBaseResFile(projectBasePath: String, moduleName: String) {
    // service interface file
    save(
        getServiceInterfaceContent(moduleName),
        File(
            projectBasePath.plus("""${File.separator}Librarys${File.separator}libBaseRes${File.separator}src${File.separator}main${File.separator}java${File.separator}com${File.separator}sandboxol${File.separator}center${File.separator}router${File.separator}moduleApi"""),
            "${moduleName}Service.kt"
        )
    )
    save(
        getModuleManagerContent(moduleName),
        File(
            projectBasePath.plus("""${File.separator}Librarys${File.separator}libBaseRes${File.separator}src${File.separator}main${File.separator}java${File.separator}com${File.separator}sandboxol${File.separator}center${File.separator}router${File.separator}manager"""),
            "${moduleName}Manager.kt"
        )
    )
}


fun addModuleInfoToModuleManager(projectPath: String, appParentPath: String, moduleName: String) {

    require(projectInstance != null) { "projectInstance 为 null" }
    require(projectInstance?.projectFile != null) { "projectFile 为 null" }

    //    projectFile = D:/source/android/blockman-go-android-innereditor/Blockymods/.idea/misc.xml

    val currentFile = projectInstance?.projectFile?.findFileByRelativePath("../../")
    // 要拿到 D:/source/android/blockman-go-android-innereditor/Blockymods
    if (currentFile == null) {
        require(false) { "打不到工程目录的 VirtualFile" }
        return
    }


    // 模块添加到 ModuleManager 的路由信息
    addRouterServiceInnerClass(projectInstance!!, currentFile, moduleName)
    // 模块添加到 App 类常量和初始化入口
    addModuleAppConstAndAppendInitEntry(projectInstance!!, currentFile, moduleName)

    // 模块添加到 setting.gradle 中
    insertIncludeAfterApp(
        Path.of(appParentPath.plus("""${File.separator}settings.gradle""")),
        moduleName
    )

    // 将模块添加到app 模块的 build.gradle 依赖中
    addModuleDependency(
        Path.of(appParentPath.plus("""${File.separator}app${File.separator}build.gradle""")),
        moduleName.lowercase()
    )
}
