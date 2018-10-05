package ec.kleber.tesis.tracker.business

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import ec.kleber.tesis.tracker.data.ApiSync
import ec.kleber.tesis.tracker.data.AppDatabase
import ec.kleber.tesis.tracker.ui.MainViewModel
import java.io.IOException
import java.util.*

class SyncWorker(context: Context, params: WorkerParameters)
    : Worker(context, params)
{
    override fun doWork(): Result
    {
        val sharedPreferences = applicationContext.getSharedPreferences(MainViewModel.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt(MainViewModel.CURRENT_USER, 0)
        val db = AppDatabase.getInstance(applicationContext)
        val unSyncedLocations = db!!.locationDao().getUnSyncedLocations(userId)

        val apiLocations = mutableListOf<ApiSync>()
        for (location in unSyncedLocations)
        {
            apiLocations.add(ApiSync(
                    location.timestamp,
                    location.latitude,
                    location.longitude,
                    location.accuracy
            ))
        }

        // post request
        val credential = sharedPreferences.getString(MainViewModel.CURRENT_TOKEN, "")
        val apiClient = ApiClient.newInstance(credential!!)
        val syncCall = apiClient.postLocationUpdates(apiLocations.toList())
        return try {
            val syncResponse = syncCall.execute()
            if (syncResponse.isSuccessful) {
                val maxLocationId = unSyncedLocations.maxBy { usl -> usl.id }!!.id
                db.locationDao().updateSyncedAt(Date(System.currentTimeMillis()), userId, maxLocationId)
                Result.SUCCESS
            } else
                Result.FAILURE
        } catch (ioEx: IOException) {
            Result.FAILURE
        }
    }
}