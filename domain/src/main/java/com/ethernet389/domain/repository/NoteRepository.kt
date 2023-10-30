package com.ethernet389.domain.repository

import com.ethernet389.domain.model.note.BaseNote
import com.ethernet389.domain.model.note.Note

interface NoteRepository {
    suspend fun deleteNote(note: Note): Boolean
    suspend fun createNote(note: BaseNote): Boolean
    suspend fun getNotes(): List<Note>
    suspend fun deleteAllNotes()
}