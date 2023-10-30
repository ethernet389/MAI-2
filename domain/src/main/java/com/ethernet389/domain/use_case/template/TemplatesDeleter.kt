package com.ethernet389.domain.use_case.template

import com.ethernet389.domain.model.template.Template
import com.ethernet389.domain.repository.TemplateRepository

class TemplatesDeleter(
    private val templateRepository: TemplateRepository
) {
    suspend fun deleteTemplate(template: Template): Boolean {
        return templateRepository.deleteTemplate(template)
    }

    suspend fun deleteUnusedTemplates() {
        return templateRepository.deleteUnusedTemplate()
    }
}