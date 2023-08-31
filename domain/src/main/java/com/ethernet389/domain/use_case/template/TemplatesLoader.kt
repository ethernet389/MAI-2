package com.ethernet389.domain.use_case.template

import com.ethernet389.domain.model.template.Template
import com.ethernet389.domain.repository.TemplateRepository

class TemplatesLoader(
    private val templateRepository: TemplateRepository
) {
    suspend fun getTemplates(): List<Template> {
        return templateRepository.getTemplates()
    }
}