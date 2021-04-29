package com.owlsoft.timetoroll

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.owlsoft.shared.model.TickData
import com.owlsoft.shared.remote.RemoteEncounterTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import java.util.*

class EncounterService : Service() {

    companion object {
        const val ENCOUNTER_CHANNEL_ID = "encounter_channel_id"
        const val ENCOUNTER_CHANNEL_NAME = "Encounter Service"

        const val ONGOING_NOTIFICATION_ID = 1
    }

    private lateinit var encounterTracker: RemoteEncounterTracker

    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val code = intent?.getStringExtra("code") ?: return START_NOT_STICKY

        encounterTracker = get { parametersOf("code" to code) }

        startForeground(1, createNotification(this@EncounterService))

        ioScope.launch {
            encounterTracker
                .data()
                .catch {
                    Log.e("error", "EncounterService. Tracker Failed")
                    stopForeground(true)
                }
                .collectLatest {
                    notificationManager.notify(
                        ONGOING_NOTIFICATION_ID,
                        it.toNotificationUpdate(this@EncounterService)
                    )
                }
        }

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::encounterTracker.isInitialized)
            encounterTracker.complete()
    }

    private fun createNotification(context: Context): Notification {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(ENCOUNTER_CHANNEL_ID, ENCOUNTER_CHANNEL_NAME)
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(getString(R.string.notification_title))
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val service = getSystemService<NotificationManager>()
        service!!.createNotificationChannel(channel)
        return channelId
    }

    private fun TickData.toNotificationUpdate(context: Context): Notification {
        val builder = NotificationCompat.Builder(context, ENCOUNTER_CHANNEL_ID)
        val participantName = participants[turnIndex].name

        val notificationTitle = resources.getString(
            R.string.encounter_notification_title,
            if (isPaused) {
                "paused"
            } else {
                "running"
            }
        )
        val notificationMessage = getString(
            R.string.encounter_notification_message,
            participantName,
            decoratedTick
        )

        if (tick <= 5) {
            builder.priority = NotificationCompat.PRIORITY_HIGH
            builder.setDefaults(Notification.DEFAULT_ALL)
        } else {
            builder.priority = NotificationCompat.PRIORITY_LOW
            builder.setNotificationSilent()
        }

        return builder
            .setNotificationSilent()
            .setContentTitle(notificationTitle)
            .setContentText(notificationMessage)
            .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
            .build()
    }
}