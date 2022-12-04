package com.bersyte.noteapp.fragmentos


import android.R
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.databinding.FgAgregarTareaBinding


var notificationID = 1
 val tituloExtra2 ="Tarea Pendiente"
const val mensajeExtra2 = "messageExtra"

class MiReceiverParaAlarma : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationUtils = NotificationUtils(context)
        val notification = notificationUtils.getNotificationBuilder(tituloExtra2).build()
        notificationUtils.getManager().notify(notificationID++, notification)
    }
}