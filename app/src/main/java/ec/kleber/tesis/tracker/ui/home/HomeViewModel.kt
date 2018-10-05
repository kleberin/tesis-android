package ec.kleber.tesis.tracker.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ec.kleber.tesis.tracker.data.AppDatabase
import ec.kleber.tesis.tracker.ui.MainViewModel
import java.util.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _lastLogin = MutableLiveData<Date>()
    val lastLogin: LiveData<Date>
    get() = _lastLogin

    private val _lastLocationTimestamp = MutableLiveData<Date?>()
    val lastLocationTimestamp: LiveData<Date?>
        get() = _lastLocationTimestamp

    private val _lastSyncTimestamp = MutableLiveData<Date?>()
    val lastSyncTimestamp: LiveData<Date?>
        get() = _lastSyncTimestamp

    fun loadInfo()
    {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(MainViewModel.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        _lastLogin.value = Date(sharedPreferences.getLong(MainViewModel.LAST_LOGIN, 0))

        val runnable = Runnable {
            val db = AppDatabase.getInstance(getApplication())
            _lastLocationTimestamp.postValue(db!!.locationDao().getLastLocationTimestamp())
            _lastSyncTimestamp.postValue(db.locationDao().getLastSyncTimestamp())
        }
        Thread(runnable).start()
    }
}
