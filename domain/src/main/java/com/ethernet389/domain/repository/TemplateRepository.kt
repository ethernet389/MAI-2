package com.ethernet389.domain.repository

import com.ethernet389.domain.model.template.BaseTemplate
import com.ethernet389.domain.model.template.Template

interface TemplateRepository {
    suspend fun deleteTemplate(template: Template): Boolean
    suspend fun createTemplate(template: BaseTemplate): Boolean
    suspend fun getTemplates(): List<Template>
    suspend fun deleteUnusedTemplate()
}