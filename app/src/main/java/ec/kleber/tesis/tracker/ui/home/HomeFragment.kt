package ec.kleber.tesis.tracker.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.*
import ec.kleber.tesis.tracker.R
import ec.kleber.tesis.tracker.business.LocationWorker
import ec.kleber.tesis.tracker.business.SyncWorker
import ec.kleber.tesis.tracker.ui.MainViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment() {

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 1
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        viewModel.lastLogin.observe(this, Observer {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                textView.text = Html.fromHtml(resources.getString(R.string.logged_in_since, viewModel.lastLogin.value))
            else
                textView.text = Html.fromHtml(resources.getString(R.string.logged_in_since, viewModel.lastLogin.value), 0)
        })
        viewModel.lastLocationTimestamp.observe(this, Observer {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                textView2.text = Html.fromHtml(resources.getString(R.string.last_update, viewModel.lastLocationTimestamp.value))
            else
                textView2.text = Html.fromHtml(resources.getString(R.string.last_update, viewModel.lastLocationTimestamp.value), 0)
        })
        viewModel.lastSyncTimestamp.observe(this, Observer {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                textView3.text = Html.fromHtml(resources.getString(R.string.last_sync, viewModel.lastSyncTimestamp.value))
            else
                textView3.text = Html.fromHtml(resources.getString(R.string.last_sync, viewModel.lastSyncTimestamp.value), 0)
        })

        viewModel.loadInfo()

        button2.setOnClickListener {
            ViewModelProviders.of(activity!!).get(MainViewModel::class.java).logOut()
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }
        }
        else {
            startLocationWorker()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    startLocationWorker()
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private fun startLocationWorker() {
        val sharedPreferences = context!!.getSharedPreferences(MainViewModel.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (!sharedPreferences.contains(MainViewModel.LOCATION_WORK_UUID)) {
            val locationWork = PeriodicWorkRequestBuilder<LocationWorker>(5, TimeUnit.MINUTES)
                    .build()

            val editor = sharedPreferences.edit()
            editor.putString(MainViewModel.LOCATION_WORK_UUID, locationWork.id.toString())
            editor.apply()

            WorkManager.getInstance().enqueue(locationWork)
        }
        if (!sharedPreferences.contains(MainViewModel.SYNC_WORK_UUID)) {
            val syncConstraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(5, TimeUnit.MINUTES)
                    .setConstraints(syncConstraints)
                    .build()

            val editor = sharedPreferences.edit()
            editor.putString(MainViewModel.SYNC_WORK_UUID, syncWork.id.toString())
            editor.apply()

            WorkManager.getInstance().enqueue(syncWork)
        }
    }
}
