package ec.kleber.tesis.tracker.business

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import ec.kleber.tesis.tracker.data.AppDatabase
import ec.kleber.tesis.tracker.ui.MainViewModel
import java.util.*

class LocationTracker(val context: Context, private val looper: Looper) : Observable() {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val userId: Int

    init {
        val sharedPreferences = context.getSharedPreferences(MainViewModel.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        userId = sharedPreferences.getInt(MainViewModel.CURRENT_USER, 0)
    }

    fun start() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if (location!!.accuracy < 30) {
                        locationManager.removeUpdates(this)
                        val newLocation = ec.kleber.tesis.tracker.data.Location()
                        newLocation.accuracy = location.accuracy
                        newLocation.latitude = location.latitude
                        newLocation.longitude = location.longitude
                        newLocation.timestamp = Date(System.currentTimeMillis())
                        newLocation.userId = userId
                        val db = AppDatabase.getInstance(context)
                        db!!.locationDao().insertAll(newLocation)
                        setChanged()
                        notifyObservers()
                    }
                }

                override fun onProviderDisabled(provider: String?) { }

                override fun onProviderEnabled(provider: String?) { }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) { }
            }, looper)
        }
        catch (se: SecurityException) { }
    }
}