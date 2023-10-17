package com.ethernet389.domain.model.template

class Template(
    val id: Long,
    name: String,
    criteria: List<String>
) : BaseTemplate(name, criteria) {
    constructor(name: String, criteria: List<String>) : this(0, name, criteria)
    constructor() : this("", emptyList())
}
