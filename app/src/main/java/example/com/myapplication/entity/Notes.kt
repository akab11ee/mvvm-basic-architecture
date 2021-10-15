package example.com.myapplication.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
public class Notes {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var title: String? = null
    var description: String? = null
    var priority: Int = 0
}