package com.github.lcj0304.sandboxmvvm.listeners

import com.github.lcj0304.sandboxmvvm.services.MyProjectService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

/**
 * @description ：
 * @author :liucj
 * @date : 2023/4/28 09:38
 */
internal class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        projectInstance = project
        project.getService(MyProjectService::class.java)
    }

    override fun projectClosing(project: Project) {
        projectInstance = null
        super.projectClosing(project)
    }

    companion object {
        var projectInstance : Project ?= null
    }

}