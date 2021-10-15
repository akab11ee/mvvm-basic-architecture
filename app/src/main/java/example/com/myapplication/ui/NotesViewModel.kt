package example.com.myapplication.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import example.com.myapplication.entity.Notes
import example.com.myapplication.repository.NotesRepository
import javax.inject.Inject

class NotesViewModel @Inject constructor() : ViewModel() {

    var notesLiveData: LiveData<List<Notes>>? = null

    fun insertNotes(context: Context, notes: Notes) {
        NotesRepository.insertNotes(context, notes)
    }

    fun deleteNotes(context: Context, notes: Notes) {
        NotesRepository.deleteNotes(context, notes)
    }

    fun getAllNotes(context: Context): LiveData<List<Notes>>? {
        notesLiveData = NotesRepository.getAllNotes(context)
        return notesLiveData
    }
}