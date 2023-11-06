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
    indices = [
        Index("_id", name = "note_id_index", unique = true),
        Index("_name", name = "note_name_index", unique = true),
        Index("template_id", name = "note_template_id_index")
    ],
    foreignKeys = [ForeignKey(
        entity = DataTemplate::class,
        parentColumns = ["_id"],
        childColumns = ["template_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class DataNote(
    @ColumnInfo(name = "_id") @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "_name") val name: String,
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