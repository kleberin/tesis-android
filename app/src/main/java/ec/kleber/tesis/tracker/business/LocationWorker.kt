package ec.kleber.tesis.tracker.business

import android.content.Context
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*
import java.util.concurrent.CountDownLatch

class LocationWorker(context: Context, params: WorkerParameters)
    :Worker(context, params), Observer
{
    companion object {
        const val TAG = "LocationWorker"
    }

    private lateinit var handlerThread: HandlerThread
    private lateinit var looper: Looper
    private lateinit var locationTracker: LocationTracker
    private lateinit var locationWait: CountDownLatch

    override fun doWork(): Result
    {
        Log.d(TAG, "doWork: Started to work")
        handlerThread = HandlerThread("MyHandlerThread")
        handlerThread.start()
        looper = handlerThread.looper
        locationTracker = LocationTracker(applicationContext, looper)
        locationTracker.addObserver(this)
        locationTracker.start()
        try {
            locationWait = CountDownLatch(1)
            locationWait.await()
            Log.d(TAG, "doWork: Countdown released")
        } catch (e: InterruptedException) {
            Log.d(TAG, "doWork: CountdownLatch interrupted")
            e.printStackTrace()
            return Result.failure()
        }

        cleanUp()
        return Result.success()
    }

    override fun update(o: Observable?, arg: Any?) {
        if (o is LocationTracker) {
            locationWait.countDown()
        }
    }

    private fun cleanUp() {
        Log.d(TAG, "Work is done");
        locationTracker.deleteObserver(this)
        looper.quit()
        handlerThread.quit()
    }
}