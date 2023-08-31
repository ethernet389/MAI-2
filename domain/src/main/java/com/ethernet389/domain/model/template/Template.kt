package com.ethernet389.domain.model.template

class Template(
    val id: Long,
    name: String,
    criteria: List<String>
) : BaseTemplate(name, criteria)
