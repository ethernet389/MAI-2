package com.ethernet389.domain.use_case.template

import com.ethernet389.domain.model.template.BaseTemplate
import com.ethernet389.domain.repository.TemplateRepository

class TemplatesCreator(
    private val templateRepository: TemplateRepository
) {
    suspend fun createTemplate(template: BaseTemplate): Boolean {
        return templateRepository.createTemplate(template)
    }
}
