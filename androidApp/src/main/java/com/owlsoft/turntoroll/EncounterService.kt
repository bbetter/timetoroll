package com.owlsoft.turntoroll

import android.R
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.owlsoft.shared.model.EncounterData
import com.owlsoft.shared.remote.RemoteEncounterTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class EncounterService : Service() {

    companion object {
        const val ONGOING_NOTIFICATION_ID = 1
        const val CHANNEL_ID = "1"
    }

    private val timerFormatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    private lateinit var encounterTracker: RemoteEncounterTracker
    private val localRemoteEncounterTracker by inject<TrackerChannelsHolder>()

    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val code = intent?.getStringExtra("code") ?: return START_NOT_STICKY
        encounterTracker = get { parametersOf("code" to code) }

        startForeground(1, createNotification(this@EncounterService))

        ioScope.launch {
            localRemoteEncounterTracker.commands.consumeEach {
                try {
                    when (it) {
                        "skip" -> encounterTracker.skipTurn()
                        "resume" -> encounterTracker.resume()
                        "pause" -> encounterTracker.pause()
                    }
                } catch (ex: Exception) {
                    stopSelf()
                }
            }
        }

        ioScope.launch {

            encounterTracker
                .data()
                .catch { stopForeground(true) }
                .collectLatest {

                    notificationManager.notify(
                        ONGOING_NOTIFICATION_ID,
                        it.toNotification(this@EncounterService)
                    )

                    localRemoteEncounterTracker.data.send(it)
                }
        }
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        encounterTracker.complete()
    }

    private fun createNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Encounter")
            .setSmallIcon(R.drawable.ic_input_get)
            .build()
    }

    private fun EncounterData.toNotification(context: Context): Notification {

        val status = if (isPaused) "paused" else "running"
        val timer = timerFormatter.format(tick * 1000)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Encounter is $status")
            .setContentText("${participants[turnIndex].name} turn. $timer")
            .setSmallIcon(R.drawable.ic_input_get)
            .build()
    }

}