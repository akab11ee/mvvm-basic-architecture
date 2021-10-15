package example.com.myapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import example.com.myapplication.R
import example.com.myapplication.di.Injector
import example.com.myapplication.entity.Notes
import kotlinx.android.synthetic.main.activity_notes.*

class NotesActivity : AppCompatActivity() {


    private val notesViewModel: NotesViewModel by lazy {
        ViewModelProviders.of(this, Injector.get().notesViewModelFactory())
            .get(NotesViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        fillNotesInDataBase()
        fillUI()

    }

    private fun fillUI() {
        val notes = notesViewModel.getAllNotes(this)?.observe(this, Observer {
            fillRecyclerView(it)
        })
    }

    private fun fillRecyclerView(list: List<Notes>) {
        recycleView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = NotesViewAdapter(list, object : NotesViewAdapter.DeleteButtonClickedListener {
                override fun onDeleteButtonClicked(notes: Notes) {
                    notesViewModel.deleteNotes(context, notes)
                }
            })
        }
    }

    private fun fillNotesInDataBase() {
        val notes1 = Notes()
        notes1.title = "Title"
        notes1.description = "First Note"
        notes1.priority = 1

        val notes2 = Notes()
        notes2.title = "Title 2"
        notes2.description = "Second Note"
        notes2.priority = 2
        notesViewModel.insertNotes(this, notes2)
        notesViewModel.insertNotes(this, notes1)
    }
}