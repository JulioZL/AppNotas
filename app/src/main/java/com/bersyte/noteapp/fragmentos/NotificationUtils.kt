package com.bersyte.noteapp.fragmentos

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R


class  NotificationUtils(base: Context) : ContextWrapper(base) {

    val MYCHANNEL_ID = "App Alert Notification ID"
    val MYCHANNEL_NAME = "App Alert Notification"

    private var manager: NotificationManager? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    // Create channel for Android version 26+
    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val channel = NotificationChannel(MYCHANNEL_ID, MYCHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        channel.enableVibration(true)
        getManager().createNotificationChannel(channel)
    }

    // Get Manager
    fun getManager() : NotificationManager {
        if (manager == null) manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager as NotificationManager
    }

    fun getNotificationBuilder(titulo:String, notificacionid:Int, descripcion:String): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtras(// (2)
                bundleOf( // (3)
                    "notification_id" to notificacionid,
                    "title" to titulo,
                    "body" to descripcion
                )
            )
        }
        val contentPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run { // (4)
            addNextIntentWithParentStack(intent) // (5)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT) // (6)
        }

        val pendingIntent = PendingIntent.getActivity(this, notificationID, intent, 0)


        return NotificationCompat.Builder(applicationContext, MYCHANNEL_ID)
            .setContentTitle("$titulo")
            .setContentText("Tienes una tarea pendiente.")
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentIntent(contentPendingIntent) // (7)
            .setColor(Color.YELLOW)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
    }
}