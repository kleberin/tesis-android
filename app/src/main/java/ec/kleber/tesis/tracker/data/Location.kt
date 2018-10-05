package ec.kleber.tesis.tracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Location(
        @PrimaryKey(autoGenerate = true) var id: Int,
        @ColumnInfo var userId: Int,
        @ColumnInfo var timestamp: Date,
        @ColumnInfo var latitude: Double,
        @ColumnInfo var longitude: Double,
        @ColumnInfo var accuracy: Float,
        @ColumnInfo(name = "synced_at") var syncedAt: Date?
) {
    constructor(): this(0, 0, Date(), 0.0, 0.0, 0f, null)
}