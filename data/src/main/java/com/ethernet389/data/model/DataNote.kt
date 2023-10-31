package com.ethernet389.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ethernet389.domain.model.note.BaseNote
import com.ethernet389.domain.model.note.Note
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@Entity(
    tableName = "notes",
    indices = [Index("id")],
    foreignKeys = [ForeignKey(
        entity = DataTemplate::class,
        parentColumns = ["id"],
        childColumns = ["template_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class DataNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @SerialName("template_id") @ColumnInfo("template_id") val templateId: Long,
    val candidates: List<String>,
    val report: String
)

fun Note.toDataNote() = DataNote(
    id = id,
    name = name,
    candidates = alternatives,
    report = report,
    templateId = template.id
)

fun BaseNote.toDataNote() = DataNote(
    name = name,
    candidates = alternatives,
    report = report,
    templateId = template.id
)