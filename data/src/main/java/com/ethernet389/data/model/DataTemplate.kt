package com.ethernet389.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ethernet389.domain.model.template.BaseTemplate
import com.ethernet389.domain.model.template.Template
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "templates",
    indices = [
        Index("_id", name = "template_id_index", unique = true),
        Index("_name", name = "template_name_index", unique = true),
    ]
)
data class DataTemplate(
    @ColumnInfo(name = "_id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "_name") val name: String,
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
