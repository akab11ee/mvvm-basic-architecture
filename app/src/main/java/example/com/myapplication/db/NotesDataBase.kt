package example.com.myapplication.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import example.com.myapplication.dao.NoteDao
import example.com.myapplication.entity.Notes

@Database(entities = [Notes::class], version = 1)
abstract class NotesDataBase : RoomDatabase() {

    abstract val noteDao: NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDataBase? = null

        fun getInstance(context: Context): NotesDataBase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NotesDataBase::class.java,
                        "notes_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}