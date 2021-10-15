package example.com.myapplication.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import example.com.myapplication.entity.Notes

@Dao
public interface NoteDao {

    @Insert
    fun insert(note: Notes)

    @Update
    fun update(note: Notes)

    @Delete
    fun delete(note: Notes)

    @Query("DELETE FROM note_table")
    fun deleteAllNotes()

    @Query("SELECT * FROM note_table ORDER BY priority DESC")
    fun getAllNotes(): LiveData<List<Notes>>
}