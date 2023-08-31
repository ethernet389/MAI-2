package com.ethernet389.data.repository

import com.ethernet389.data.dao.NoteDao
import com.ethernet389.data.dao.TemplateDao
import com.ethernet389.data.model.toDataNote
import com.ethernet389.data.model.toTemplate
import com.ethernet389.domain.model.note.BaseNote
import com.ethernet389.domain.model.note.Note
import com.ethernet389.domain.repository.NoteRepository

class RoomNoteRepository(
    private val noteDao: NoteDao,
    private val templateDao: TemplateDao
) : NoteRepository {
    override suspend fun deleteNote(note: Note): Boolean {
        val dataNote = note.toDataNote()
        return noteDao.deleteNote(dataNote) != 0L
    }

    override suspend fun createNote(note: BaseNote): Boolean {
        val dataNote = note.toDataNote()
        return noteDao.insertNote(dataNote) != -1L
    }

    override suspend fun getNotes(): List<Note> {
        val dataNotes = noteDao.getAllNotes()
        val notes = dataNotes.map {
            Note(
                id = it.id,
                name = it.name,
                candidates = it.candidates,
                report = it.report,
                template = templateDao
                    .getTemplateById(id = it.templateId)
                    .toTemplate()
            )
        }
        return notes
    }
}