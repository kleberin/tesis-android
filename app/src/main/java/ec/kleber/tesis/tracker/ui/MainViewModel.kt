package ec.kleber.tesis.tracker.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.WorkManager
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application)
{
    companion object {
        const val SHARED_PREFERENCES = "SP"
        const val CURRENT_USER = "CU"
        const val CURRENT_TOKEN = "CT"
        const val LAST_LOGIN = "LL"
        const val LOCATION_WORK_UUID = "WUUID"
        const val SYNC_WORK_UUID = "SUUID"
    }

    private val _currentUserId = MutableLiveData<Int?>()
    val currentUserId: LiveData<Int?>
    get() =  _currentUserId

    fun checkUserLoggedIn()
    {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (sharedPreferences.contains(CURRENT_USER))
            _currentUserId.value = sharedPreferences.getInt(CURRENT_USER, 0)
        else
            _currentUserId.value = null
    }

    fun setCurrentUserData(currentUserId: Int, currentUserToken: String)
    {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(CURRENT_USER, currentUserId)
        editor.putString(CURRENT_TOKEN, currentUserToken)
        editor.putLong(LAST_LOGIN, System.currentTimeMillis())
        editor.apply()
        _currentUserId.value = currentUserId
    }

    fun logOut()
    {
        val sharedPreferences = getApplication<Application>().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        val locationWorkUUID = UUID.fromString(sharedPreferences.getString(LOCATION_WORK_UUID, null))
        val syncWorkUUID = UUID.fromString(sharedPreferences.getString(SYNC_WORK_UUID, null))
        WorkManager.getInstance().cancelWorkById(locationWorkUUID)
        WorkManager.getInstance().cancelWorkById(syncWorkUUID)
        val editor = sharedPreferences.edit()
        editor.remove(CURRENT_USER)
        editor.remove(LAST_LOGIN)
        editor.remove(LOCATION_WORK_UUID)
        editor.remove(SYNC_WORK_UUID)
        editor.apply()
        _currentUserId.value = null
    }
}