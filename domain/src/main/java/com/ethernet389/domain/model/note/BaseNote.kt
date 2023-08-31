package com.ethernet389.domain.model.note

import com.ethernet389.domain.model.template.Template

open class BaseNote(
    val name: String,
    val template: Template,
    val candidates: List<String>,
    val report: String
)
