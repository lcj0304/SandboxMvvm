package com.github.lcj0304.sandboxmvvm.template

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiType
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiUtil
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


fun addRouterServiceInnerClass(
    project: Project,
    current: VirtualFile,
    moduleName: String
) {
    val elementFactory = JavaPsiFacade.getElementFactory(project)

    // 确保能找到 String 类型
    val psiType: PsiType =
        PsiType.getTypeByName("java.lang.String", project, GlobalSearchScope.allScope(project))

    val pathDir: VirtualFile? =
        current.findFileByRelativePath("../Librarys/libBaseRes/src/main/java/com/sandboxol/center/router/path")

    if (pathDir != null) {
        val children = pathDir.children ?: return
        for (vf in children) {
            if (!vf.isDirectory && vf.name == "RouterServicePath.java") {
                val psiFile =
                    PsiManager.getInstance(project).findFile(vf) as? PsiJavaFile ?: continue
                val targetClass = psiFile.classes.firstOrNull() ?: continue

                // 幂等性检查
                if (targetClass.findInnerClassByName(moduleName, false) != null) {
                    return
                }

                // 1. 创建内部类
                val innerClass = elementFactory.createClass(moduleName)
                PsiUtil.setModifierProperty(innerClass, PsiModifier.PUBLIC, true)
                PsiUtil.setModifierProperty(innerClass, PsiModifier.STATIC, true)

                // 2. 创建 BASE 字段
                val base = elementFactory.createField("BASE", psiType)
                PsiUtil.setModifierProperty(base, PsiModifier.PRIVATE, true)
                PsiUtil.setModifierProperty(base, PsiModifier.STATIC, true)
                PsiUtil.setModifierProperty(base, PsiModifier.FINAL, true)
                base.initializer = elementFactory.createExpressionFromText("\"/$moduleName\"", base)

                // 关键点1：add() 会返回添加后的 PSI 元素，将其作为锚点
                val addedBase = innerClass.add(base)

                // 3. 创建 SERVICES 字段
                val serviceNameField = elementFactory.createField("SERVICES", psiType)
                PsiUtil.setModifierProperty(serviceNameField, PsiModifier.PUBLIC, true)
                PsiUtil.setModifierProperty(serviceNameField, PsiModifier.STATIC, true)
                PsiUtil.setModifierProperty(serviceNameField, PsiModifier.FINAL, true)
                serviceNameField.initializer =
                    elementFactory.createExpressionFromText("BASE + \"/service\"", serviceNameField)

                // 关键点2：使用 addAfter 并传入 addedBase，强制 SERVICES 在 BASE 后面
                innerClass.addAfter(serviceNameField, addedBase)

                // 4. 将构建好的内部类添加到父类
                targetClass.add(innerClass)
                println("==========================>> Added Router Inner Class: $moduleName")
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
                    val initializer = initModuleArray.initializer
                    // 使用标准的 PSI 类型判断和添加操作，而不是脆弱的字符串替换
                    if (initializer is com.intellij.psi.PsiArrayInitializerExpression) {
                        // 幂等性检查：遍历现有的初始化表达式，避免重复添加
                        val alreadyExists = initializer.initializers.any { it.text == serviceName }
                        if (alreadyExists) {
                            return
                        }

                        println("==========================>> Adding $serviceName")
                        // 创建新的表达式并添加到数组初始化列表中
                        val newExpression =
                            elementFactory.createExpressionFromText(serviceName, initializer)
                        initializer.add(newExpression)
                    }
                }
            }
        }
    } else {
        require(false) { "moduleApplicationDir 不能为空: ${current.path} ../Librarys/libBaseRes/src/main/java/com/sandboxol/center/router/moduleApplication" }
    }
}
