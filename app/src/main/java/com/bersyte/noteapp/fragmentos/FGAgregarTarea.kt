package com.bersyte.noteapp.fragmentos

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bersyte.noteapp.*
import com.bersyte.noteapp.databinding.FgAgregarTareaBinding
import com.bersyte.noteapp.model.Tarea
import com.bersyte.noteapp.viewmodel.TareaViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class FGAgregarTarea : Fragment(R.layout.fg_agregar_tarea) {

    private var _binding: FgAgregarTareaBinding? = null
    private val binding get() = _binding!!
    private lateinit var tareaViewModel: TareaViewModel
    private lateinit var mView: View

    private var diaAux=0
    private var mesAux=0
    private var anioAux=0

    private var horaAux=0
    private var minutosAux=0

    //fecha
    var currentDate: String? = null

    private lateinit var fecha: EditText
    private lateinit var hora: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

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

        fecha = binding.txtDate
        hora = binding.txtHour

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

        currentDate = sdf.format(Date())
        binding.tvDateTarea.text = currentDate

        //Date and hour
        binding.date.setOnClickListener {
            showDatePickerDialog()
        }

        binding.hour.setOnClickListener {
            showTimePikerDialog()
        }

        var id=-1
        val bundle = Bundle()
        //id = arguments?.getString("id")!!.toInt()

        bundle.putString("id", id.toString())
        binding.btnFotoT.setOnClickListener {
            it.findNavController().navigate(R.id.action_createTask_to_photoFragment, bundle)
        }

        binding.btnVideoT.setOnClickListener {
            it.findNavController().navigate(R.id.action_newTareaFragment_to_videoFragment, bundle)
        }

        binding.btnAudioT.setOnClickListener {
            it.findNavController().navigate(R.id.action_newTareaFragment_to_audio, bundle)
        }

        return binding.root
    }

    private fun showTimePikerDialog() {
        val newFragment = TimePicker { hour, minute -> onTimeSelected(hour, minute) }
        activity?.let { newFragment.show(it.supportFragmentManager, "timePicker") }
    }

    private fun onTimeSelected(hour:Int, minute:Int) {
        hora.setText("$hour:$minute")
        this.horaAux=hour
        this.minutosAux=minute
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePicker { day, month, year -> onDateSelected(day, month, year) }
        activity?.let { newFragment.show(it.supportFragmentManager, "datePicker") }
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        var aux=month+1
        fecha.setText("$day/$aux/$year")

        this.diaAux=day
        this.mesAux=month
        this.anioAux=year
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
            scheduleNotificaction(tareaTitle)


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
    private fun scheduleNotificaction(titulo: String) {
        getTime(titulo)
    }

    private fun startAlarm(calendar: Calendar, titulo: String) {
        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MiReceiverParaAlarma::class.java)
        val message = "Tienes esta tarea pendiente"
        intent.putExtra(tituloExtra2, titulo)
        intent.putExtra(mensajeExtra2, message)
        val pendingIntent = PendingIntent.getBroadcast(context, notificationID, intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun getTime(titulo: String){

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,horaAux)
        calendar.set(Calendar.MINUTE,minutosAux)
        calendar.set(Calendar.SECOND,0)
        startAlarm(calendar, titulo)
    }
}