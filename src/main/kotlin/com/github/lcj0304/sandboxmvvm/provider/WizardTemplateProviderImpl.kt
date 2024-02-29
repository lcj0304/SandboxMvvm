package com.github.lcj0304.sandboxmvvm.provider

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider

/**
 * @description ：
 * @author :liucj
 * @date : 2023/4/27 21:02
 */
class WizardTemplateProviderImpl : WizardTemplateProvider() {
    override fun getTemplates(): List<Template> = listOf(fragmentGenerator, activityGenerator, listViewGenerator)
}

