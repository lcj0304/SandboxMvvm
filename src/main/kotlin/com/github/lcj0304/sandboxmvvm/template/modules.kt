package com.github.lcj0304.sandboxmvvm.template

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiField
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiType
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiUtil
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale


fun getModulesGradleContent(moduleName: String): String {
    return """
apply plugin: 'org.jetbrains.kotlin.android'
android {
    defaultConfig {
        //如果是独立模块，则使用当前组件的包名
        if (isBuildModule.toBoolean()) {
            lintOptions {
                checkReleaseBuilds false
                abortOnError false
            }
            applicationId "com.sandboxol.${moduleName.lowercase(Locale.getDefault())}"
        }
    }
}

dependencies {
    implementation project(path: ':libBaseRes')
    implementation project(path: ':BusinessEvent')
}
    """.trimIndent()
}


fun getManifestContent(moduleName: String): String {
    return """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.sandboxol.${moduleName.lowercase(Locale.getDefault())}">

    <application>
    </application>
</manifest>"""
}


fun getAppClassContent(moduleName: String): String {

    return """package com.sandboxol.${moduleName.lowercase(Locale.getDefault())}

import android.app.Application
import com.sandboxol.center.router.manager.${moduleName}Manager
import com.sandboxol.center.router.moduleApplication.IModuleInit

class ${moduleName}App : IModuleInit {
    override fun onCreate(application: Application?) {
        ${moduleName}Manager.load()
    }

    override fun onDestroy(application: Application?) {

    }
}"""
}


fun getModuleApiInterfaceContent(moduleName: String): String {
    return """package com.sandboxol.${moduleName.lowercase()}.web

interface I${moduleName}Api {


}
"""
}

fun getModuleApiContent(moduleName: String): String {
    return """package com.sandboxol.${moduleName.lowercase()}.web

import com.sandboxol.center.web.retrofit.RetrofitFactory
import com.sandboxol.common.base.app.BaseApplication

object ${moduleName}Api {

    private val api: I${moduleName}Api? = RetrofitFactory.httpsCreate(
        BaseApplication.getApp().metaDataBaseUrl,
        BaseApplication.getApp().metaDataBackupBaseUrl,
        I${moduleName}Api::class.java
    )
}"""
}

fun getModuleServiceImplContent(moduleName: String): String {
    return """package com.sandboxol.${moduleName.lowercase()}.service

import com.alibaba.android.arouter.facade.annotation.Route
import com.sandboxol.center.router.moduleApi.I${moduleName}Service
import com.sandboxol.center.router.path.RouterServicePath

@Route(path = RouterServicePath.${moduleName}.SERVICES)
class ${moduleName}Service : I${moduleName}Service {

    init {
    }


}"""
}


fun getServiceInterfaceContent(moduleName: String): String {
    return """package com.sandboxol.center.router.moduleApi

import android.content.Context
import android.os.Bundle

interface I${moduleName}Service: IBaseService {

}
"""
}


fun getModuleManagerContent(moduleName: String): String {
    return """package com.sandboxol.center.router.manager

import com.sandboxol.center.router.RouteServiceManager
import com.sandboxol.center.router.moduleApi.I${moduleName}Service
import com.sandboxol.center.router.path.RouterServicePath

object ${moduleName}Manager {

    private var service: I${moduleName}Service? = null

    fun load() {
        service = RouteServiceManager.provide(RouterServicePath.${moduleName}.SERVICES)
    }
}
"""
}


fun insertIncludeAfterApp(settingsGradle: Path, moduleName: String) {
    require(moduleName.isNotBlank()) { "moduleName 不能为空" }
    val includeLineToInsert = "include ':${moduleName.lowercase()}'"

    val original = Files.readString(settingsGradle, StandardCharsets.UTF_8)

    // 去重：已存在同样的 include 行就不再插入
    val existsRegex = Regex("""(?m)^\s*include\s+['"]:${moduleName.lowercase()}['"]\s*$""")
    if (existsRegex.containsMatchIn(original)) return

    // 统一处理换行符（保持原文件风格）
    val newline = if (original.contains("\r\n")) "\r\n" else "\n"

    // 定位 include ':app' 这一行（允许它与其他 include 同行，例如 include ':app', ':x'）
    val anchorRegex = Regex("""(?m)^\s*include\s+.*(['"]):app\1.*$""")
    val m = anchorRegex.find(original) ?: error("未找到包含 :app 的 include 行")

    // 找到锚点行的行尾（\n 或 \r\n 之后）
    val lineEnd = run {
        val afterLineStart = m.range.last + 1
        val idx = original.indexOf('\n', afterLineStart)
        if (idx >= 0) idx + 1 else original.length
    }

    val insertion = includeLineToInsert + newline
    val updated = original.substring(0, lineEnd) + insertion + original.substring(lineEnd)

    Files.writeString(settingsGradle, updated, StandardCharsets.UTF_8)
}


/**
 * 动态向 build.gradle 添加模块依赖
 *
 * @param buildGradlePath build.gradle 文件的绝对或相对路径
 * @param moduleName 需要添加的模块名称，不带冒号，例如 "roblox"
 */
fun addModuleDependency(buildGradlePath: Path, moduleName: String) {

    if (!Files.exists(buildGradlePath)) {
        println("错误: 文件未找到 -> $buildGradlePath")
        return
    }

    // 读取文件所有行
    val lines = Files.lines(buildGradlePath).toList().toMutableList()

    // 构造目标代码行 (保持缩进 4 个空格)
    val dependencyLine = "    implementation project(':$moduleName')"

    // 1. 检查幂等性：避免重复添加
    // 检查是否存在 implementation project(':moduleName') 或 implementation project(":moduleName")
    val alreadyExists = lines.any { line ->
        line.contains("project(':$moduleName')") || line.contains("project(\":$moduleName\")")
    }

    if (alreadyExists) {
        println("跳过: 依赖 [:$moduleName] 已存在于文件中。")
        return
    }


    // 优先插入到login 后面
    var insertIndex = lines.indexOfFirst {
        it.trim().contains("""project(':login')""")
    }

    if (insertIndex == -1) {
        // 2. 寻找最佳插入位置
        // 策略：找到文件中最后一行以 "implementation project" 开头的代码，插入在其下方
        // 这能保证新代码被放入 dependencies { ... } 块中，且保持大致的格式一致
        insertIndex = lines.indexOfLast {
            it.trim().startsWith("implementation project")
        }
    }


    if (insertIndex != -1) {
        // 在找到的行后面插入新行
        lines.add(insertIndex + 1, dependencyLine)

        // 3. 写回文件
        Files.writeString(buildGradlePath, lines.joinToString("\n"), StandardCharsets.UTF_8)
        println("成功: 已添加依赖 -> $dependencyLine")
    } else {
        println("失败: 未能在文件中找到任何 'implementation project' 语句，无法确定插入位置。")
    }
}

/**
 * 定义服务路由内部类
 */
fun addRouterServiceInnerClass(
    project: Project,
    current: VirtualFile,
    moduleName: String
) {

    val elementFactory = JavaPsiFacade.getElementFactory(project)
    val psiType: PsiType =
        PsiType.getTypeByName("java.lang.String", project, GlobalSearchScope.EMPTY_SCOPE)

    val pathDir: VirtualFile? =
        current.findFileByRelativePath("../Librarys/libBaseRes/src/main/java/com/sandboxol/center/router/path")

    if (pathDir != null) {
        val children = pathDir.children ?: return
        for (vf in children) {
            if (!vf.isDirectory && vf.name == "RouterServicePath.java") {
                val psiFile = PsiManager.getInstance(project).findFile(vf)
                val psiJavaFile = psiFile as PsiJavaFile?
                val classes = psiJavaFile!!.classes
                var psiClass = classes[0].findInnerClassByName(moduleName, false)
                if (psiClass == null) {
                    val base: PsiField = elementFactory.createField("BASE", psiType)
                    PsiUtil.setModifierProperty(base, PsiModifier.PRIVATE, true)
                    PsiUtil.setModifierProperty(base, PsiModifier.STATIC, true)
                    PsiUtil.setModifierProperty(base, PsiModifier.FINAL, true)
                    val baseExpression: PsiExpression =
                        elementFactory.createExpressionFromText("\"/$moduleName\"", null)
                    base.initializer = baseExpression

                    val serviceName: PsiField =
                        elementFactory.createField("SERVICES", psiType)
                    PsiUtil.setModifierProperty(serviceName, PsiModifier.PUBLIC, true)
                    PsiUtil.setModifierProperty(serviceName, PsiModifier.STATIC, true)
                    PsiUtil.setModifierProperty(serviceName, PsiModifier.FINAL, true)
                    val serviceExpression: PsiExpression =
                        elementFactory.createExpressionFromText("BASE + \"/service\"", null)
                    serviceName.initializer = serviceExpression

                    val baseText = base.text
                    val serviceNameText = serviceName.text

                    psiClass = elementFactory.createClassFromText(
                        """
                            $baseText
                            $serviceNameText
                            """.trimIndent(), null
                    )
                    psiClass.setName(moduleName)
                    PsiUtil.setModifierProperty(psiClass, PsiModifier.PUBLIC, true)
                    PsiUtil.setModifierProperty(psiClass, PsiModifier.STATIC, true)

                    classes[0].add(psiClass)
                }
            }
        }
    } else {
        require(false) { "pathDir 不能为空: ${current.path} ../Librarys/libBaseRes/src/main/java/com/sandboxol/center/router/path" }
    }
}


fun addModuleAppConstAndAppendInitEntry(
    project: Project,
    current: VirtualFile,
    moduleName: String
) {

    val elementFactory = JavaPsiFacade.getElementFactory(project)
    val psiType: PsiType =
        PsiType.getTypeByName("java.lang.String", project, GlobalSearchScope.EMPTY_SCOPE)

    val moduleApplicationDir: VirtualFile? =
        current.findFileByRelativePath("../Librarys/libBaseRes/src/main/java/com/sandboxol/center/router/moduleApplication")
    if (moduleApplicationDir != null) {
        val children = moduleApplicationDir.children ?: return
        for (vf in children) {
            if (!vf.isDirectory && vf.name == "ModuleReflexs.java") {
                val psiFile = PsiManager.getInstance(project).findFile(vf)
                val psiJavaFile = psiFile as PsiJavaFile?
                val classes = psiJavaFile!!.classes
                val serviceName = "${moduleName.uppercase()}_SERVICE_APP"
                var serviceApp = classes[0].findFieldByName(serviceName, false)
                if (serviceApp == null) {
                    serviceApp = elementFactory.createField(serviceName, psiType)
                    PsiUtil.setModifierProperty(serviceApp, PsiModifier.PRIVATE, true)
                    PsiUtil.setModifierProperty(serviceApp, PsiModifier.STATIC, true)
                    PsiUtil.setModifierProperty(serviceApp, PsiModifier.FINAL, true)

                    val psiExpression = elementFactory.createExpressionFromText(
                        "\"com.sandboxol.${moduleName.lowercase()}.${moduleName}App\"",
                        null
                    )
                    serviceApp.initializer = psiExpression
                    classes[0].add(serviceApp)
                }

                val initModuleNames = "initModuleNames"
                val initModuleArray = classes[0].findFieldByName(initModuleNames, false)
                if (initModuleArray != null) {
                    val copy = initModuleArray.copy() as PsiField

                    val initializer = copy.initializer
                    val text = initializer!!.text
                    // 已经包含了该条目就不再添加， 过滤一下，不知道为啥会添加两次
                    if (text.contains(serviceName)) {
                        return
                    }
                    initModuleArray.delete()
                    val replace = text.replace("}", ",\n            $serviceName}")
                    println("==========================>>")
                    println("replace=$text")
                    val psiExpression = elementFactory.createExpressionFromText(replace, null)
                    copy.initializer = psiExpression
                    classes[0].add(copy)
                }
            }
        }
    } else {
        require(false) { "moduleApplicationDir 不能为空: ${current.path} ../Librarys/libBaseRes/src/main/java/com/sandboxol/center/router/moduleApplication" }
    }
}
