package ec.kleber.tesis.tracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import java.util.*

@Dao
interface LocationDao {

    @Query("select max(timestamp) from Location")
    fun getLastLocationTimestamp(): Date

    @Query("select max(synced_at) from Location")
    fun getLastSyncTimestamp(): Date

    @Query("select * from Location where userId = :userId and synced_at is null")
    fun getUnSyncedLocations(userId: Int): List<Location>

    @Insert
    fun insertAll(vararg location: Location)

    @Update
    fun updateLocation(location: Location)

    @Query("update Location set synced_at = :syncedAt where synced_at is null and userId = :userId and id <= :maxId")
    fun updateSyncedAt(syncedAt: Date?, userId: Int, maxId: Int): Int
}