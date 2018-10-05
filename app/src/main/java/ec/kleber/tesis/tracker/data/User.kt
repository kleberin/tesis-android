package ec.kleber.tesis.tracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo var email: String,
    @ColumnInfo var name: String,
    @ColumnInfo var token: String
) {
    constructor() : this(0, "","", "")
}