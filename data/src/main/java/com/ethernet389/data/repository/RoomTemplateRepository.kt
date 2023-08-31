package com.ethernet389.data.repository

import com.ethernet389.data.dao.TemplateDao
import com.ethernet389.data.model.DataTemplate
import com.ethernet389.data.model.toDataTemplate
import com.ethernet389.data.model.toTemplate
import com.ethernet389.domain.model.template.BaseTemplate
import com.ethernet389.domain.model.template.Template
import com.ethernet389.domain.repository.TemplateRepository

class RoomTemplateRepository(
    private val templateDao: TemplateDao
) : TemplateRepository {
    override suspend fun deleteTemplate(template: Template): Boolean {
        return templateDao.deleteTemplate(template.toDataTemplate()) != 0L
    }

    override suspend fun createTemplate(template: BaseTemplate): Boolean {
        return templateDao.insertTemplate(template.toDataTemplate()) != -1L
    }

    override suspend fun getTemplates(): List<Template> {
        return templateDao.getAllTemplates().map(DataTemplate::toTemplate)
    }
}