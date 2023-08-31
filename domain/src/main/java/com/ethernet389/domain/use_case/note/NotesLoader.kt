package com.ethernet389.domain.use_case.note

import com.ethernet389.domain.model.note.Note
import com.ethernet389.domain.repository.NoteRepository

class NotesLoader(
    private val noteRepository: NoteRepository
) {
    suspend fun loadNotes(): List<Note> {
        return noteRepository.getNotes()
    }
}