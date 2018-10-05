package ec.kleber.tesis.tracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ec.kleber.tesis.tracker.ui.MainViewModel
import ec.kleber.tesis.tracker.ui.home.HomeFragment
import ec.kleber.tesis.tracker.ui.login.LoginFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        mModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        mModel.currentUserId.observe(this, Observer {
            if (it == null)
            {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, LoginFragment.newInstance())
                        .commitNow()
            }
            else
            {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance())
                        .commitNow()
            }
        })

        if (savedInstanceState == null) {
            mModel.checkUserLoggedIn()
        }
    }
}
