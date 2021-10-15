package example.com.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import example.com.myapplication.R
import example.com.myapplication.entity.Notes
import kotlinx.android.synthetic.main.listitem_notes.view.*

class NotesViewAdapter(
    private val listNotes: List<Notes>,
    private val listener: DeleteButtonClickedListener
) :
    RecyclerView.Adapter<NotesViewAdapter.NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val row =
            LayoutInflater.from(parent.context).inflate(R.layout.listitem_notes, parent, false)
        return NotesViewHolder(row)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.deleteButton?.setOnClickListener {
            listener.onDeleteButtonClicked(listNotes[position])
        }

        holder.textViewNotes?.text = listNotes[position].title
        holder.textViewDescription?.text = listNotes[position].description
    }

    override fun getItemCount(): Int {
        return listNotes.size
    }

    interface DeleteButtonClickedListener {
        fun onDeleteButtonClicked(notes: Notes)
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNotes: TextView? = itemView.notes
        val textViewDescription: TextView? = itemView.notesDescription
        val deleteButton: Button? = itemView.btnDelete
    }
}