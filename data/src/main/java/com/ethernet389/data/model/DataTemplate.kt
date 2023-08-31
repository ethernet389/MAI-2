package com.ethernet389.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ethernet389.domain.model.template.BaseTemplate
import com.ethernet389.domain.model.template.Template
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "templates")
data class DataTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val criteria: List<String>,
)

fun Template.toDataTemplate() = DataTemplate(
    id = id, name = name, criteria = criteria
)

fun BaseTemplate.toDataTemplate() = DataTemplate(
    name = name, criteria = criteria
)

fun DataTemplate.toTemplate() = Template(
    id = id, name = name, criteria = criteria
)
