package com.bersyte.noteapp.fragmentos

import android.app.*
import android.app.Notification
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bersyte.noteapp.*
import com.bersyte.noteapp.databinding.FgAgregarTareaBinding
import com.bersyte.noteapp.db.TareaDatabase
import com.bersyte.noteapp.model.Tarea
import com.bersyte.noteapp.viewmodel.TareaViewModel
import com.example.proyecto_notas.channelID
import com.example.proyecto_notas.messageExtra
import com.example.proyecto_notas.titleExtra
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


class FGAgregarTarea : Fragment(R.layout.fg_agregar_tarea) {

    private var _binding: FgAgregarTareaBinding? = null
    private val binding get() = _binding!!
    private lateinit var tareaViewModel: TareaViewModel
    private lateinit var mView: View


    //fecha
    var currentDate:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        //createNotificationChannel()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FgAgregarTareaBinding.inflate(
            inflater,
            container,
            false
        )

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

        currentDate = sdf.format(Date())
        binding.tvDateTarea.text = currentDate

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tareaViewModel = (activity as MainActivity).tareaViewModel
        mView = view
    }

    private fun saveTarea(view: View) {
        val tareaTitle = binding.etTareaTitle.text.toString().trim()
        val tareaSubTitle = binding.etTareaSubTitle.text.toString().trim()
        val tareatvDate = binding.tvDateTarea.text.toString().trim()
        val tareaBody = binding.etTareaBody.text.toString().trim()

        if (tareaTitle.isNotEmpty()) {
            val tarea = Tarea(0, tareaTitle, tareaSubTitle, tareatvDate, tareaBody)
            scheduleNotification(tareaTitle)
            createNotificationChannel()

            tareaViewModel.agregarTarea(tarea)
            Snackbar.make(
                view, "Nota guardada.",
                Snackbar.LENGTH_SHORT
            ).show()
            //arreglar grafo
            view.findNavController().navigate(R.id.action_newTareaFragment_to_homeFragment)

        } else {
            activity?.toast("Ingresa un Titulo")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_agregar_nota, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> {
                saveTarea(mView)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotification(id:String){
        val intent  = Intent(context, Notification::class.java)
        val title =   "Recordatorio"
        val message = "¡Recuerda hacer tu tarea!"
        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        var alarmManager =  activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            0
        )
        val time = getTime()
        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            //time,
            SystemClock.elapsedRealtime()+10*1000,
            pendingIntent
        )
        showAlert(SystemClock.elapsedRealtime()+10*1000,title,message)
    }

    private fun showAlert(time: Long, title: String, message: String)
    {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireContext().applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext().applicationContext)

        AlertDialog.Builder(this@FGAgregarTarea.requireContext())
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date))
            .setPositiveButton("Okay"){_,_ ->}
            .show()
    }

    private fun getTime(): Long
    {
        val minute = binding.timePicker.minute
        val hour = binding.timePicker.hour
        val day = binding.datePicker.dayOfMonth
        val month = binding.datePicker.month
        val year = binding.datePicker.year

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        //val notificationManager = NotificationManagerCompat.from(requireContext().applicationContext)
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


}