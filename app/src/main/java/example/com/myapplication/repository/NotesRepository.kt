package example.com.myapplication.repository

import android.content.Context
import androidx.lifecycle.LiveData
import example.com.myapplication.db.NotesDataBase
import example.com.myapplication.entity.Notes
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class NotesRepository {

    companion object {
        var notesDataBase: NotesDataBase? = null
        var listNotes: LiveData<List<Notes>>? = null

        private fun initializeDB(context: Context): NotesDataBase {
            return NotesDataBase.getInstance(context)
        }

        fun insertNotes(context: Context, notes: Notes) {
            notesDataBase = initializeDB(context)
            CoroutineScope(IO).launch {
                notesDataBase!!.noteDao.insert(notes)
            }
        }

        fun updateNotes(context: Context, notes: Notes) {
            notesDataBase = initializeDB(context)
            CoroutineScope(IO).launch {
                notesDataBase!!.noteDao.update(notes)
            }
        }

        fun deleteNotes(context: Context, notes: Notes) {
            notesDataBase = initializeDB(context)
            CoroutineScope(IO).launch {
                notesDataBase!!.noteDao.delete(notes)
            }
        }

        fun getAllNotes(context: Context): LiveData<List<Notes>>? {
            notesDataBase = initializeDB(context)
            CoroutineScope(IO).launch {
                listNotes = notesDataBase!!.noteDao.getAllNotes()
            }
            return listNotes
        }
    }
}