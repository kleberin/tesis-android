package ec.kleber.tesis.tracker.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ec.kleber.tesis.tracker.business.ApiClient
import ec.kleber.tesis.tracker.data.AppDatabase
import ec.kleber.tesis.tracker.data.Event
import ec.kleber.tesis.tracker.data.User
import okhttp3.Credentials
import java.io.IOException

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginSuccess = MutableLiveData<Event<Boolean>>()
    val loginSuccess: LiveData<Event<Boolean>>
    get() = _loginSuccess

    var currentUserId: Int? = null
    var currentUserToken: String? = null

    fun login(email: String, password: String)
    {
        val runnable = Runnable {
            val db = AppDatabase.getInstance(getApplication())
            val user = db!!.userDao().findByEmail(email)
            val credential = Credentials.basic(email, password)
            if (user == null)
            {
                val apiClient = ApiClient.newInstance(credential)
                try {
                    val apiResponse = apiClient.getCurrentUser().execute()
                    if (apiResponse.isSuccessful) {
                        val apiUser = apiResponse.body()!!
                        val newUser = User(apiUser.id, email = email, name = apiUser.name, token = credential)
                        db.userDao().insertAll(newUser)
                        currentUserId = newUser.id
                        currentUserToken = credential
                        _loginSuccess.postValue(Event(true))
                        return@Runnable
                    }
                } catch (ioEx: IOException) {
                    Log.d("LoginIOException", ioEx.message)
                }
            }
            else
            {
                if (credential == user.token)
                {
                    currentUserId = user.id
                    currentUserToken = user.token
                    _loginSuccess.postValue(Event(true))
                    return@Runnable
                }
            }

            _loginSuccess.postValue(Event(false))
        }
        Thread(runnable).start()
    }
}
