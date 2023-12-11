package com.ethernet389.data.repository

import com.ethernet389.data.dao.TemplateDao
import com.ethernet389.data.model.DataTemplate
import com.ethernet389.data.model.toDataTemplate
import com.ethernet389.data.model.toTemplate
import com.ethernet389.domain.model.template.BaseTemplate
import com.ethernet389.domain.model.template.Template
import com.ethernet389.domain.repository.TemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomTemplateRepository(
    private val templateDao: TemplateDao
) : TemplateRepository {
    override suspend fun deleteTemplate(template: Template): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext templateDao.deleteTemplate(template.toDataTemplate()) != 0
        }
    }

    override suspend fun createTemplate(template: BaseTemplate): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext templateDao.insertTemplate(template.toDataTemplate()) != -1L
        }
    }

    override suspend fun getTemplates(): List<Template> {
        return withContext(Dispatchers.IO) {
            return@withContext templateDao.getAllTemplates().map(DataTemplate::toTemplate)
        }
    }

    override suspend fun deleteUnusedTemplates() {
        return withContext(Dispatchers.IO) {
            templateDao.deleteUnusedTemplates()
        }
    }
}