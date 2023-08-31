package com.ethernet389.domain.model.note

import com.ethernet389.domain.model.template.Template

class Note(
    val id: Long,
    name: String,
    template: Template,
    candidates: List<String>,
    report: String
) : BaseNote(name, template, candidates, report)
