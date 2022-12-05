package com.bersyte.noteapp.fragmentos

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R
import com.bersyte.noteapp.databinding.FgActualizarTareaBinding
import com.bersyte.noteapp.model.Tarea
import com.bersyte.noteapp.toast
import com.bersyte.noteapp.viewmodel.TareaViewModel
import java.text.SimpleDateFormat
import java.util.*


class FGActualizarTareas : Fragment(R.layout.fg_actualizar_tarea) {

    private var _binding: FgActualizarTareaBinding? = null
    private val binding get() = _binding!!

    private val args: FGActualizarTareasArgs by navArgs()
    private lateinit var currentTarea: Tarea
    private lateinit var tareaViewModel: TareaViewModel

    private var diaAux=0
    private var mesAux=0
    private var anioAux=0

    private var horaAux=0
    private var minutosAux=0

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

        _binding = FgActualizarTareaBinding.inflate(
            inflater,
            container,
            false
        )
        fecha = binding.txtDate
        hora = binding.txtHour
        //Date and hour
        binding.date.setOnClickListener {
            showDatePickerDialog()
        }

        binding.hour.setOnClickListener {
            showTimePikerDialog()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tareaViewModel = (activity as MainActivity).tareaViewModel
        currentTarea = args.tarea!!

        var currentDate:String? = null
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        currentDate = sdf.format(Date())

        binding.etTareaBodyUpdate.setText(currentTarea.tareaBody)
        binding.tvTareaDateUpdate.setText(currentDate)
        binding.etTareaSubTitleUpdate.setText(currentTarea.tareaSubTitle)
        binding.etTareaTitleUpdate.setText(currentTarea.tareaTitle)

        val bundle=Bundle()
        bundle.putString("id", currentTarea.id.toString())

        binding.btnFotoT.setOnClickListener {
            it.findNavController().navigate(R.id.action_updateTask_to_photoFragment, bundle)
        }

        binding.btnVideoT.setOnClickListener {
            it.findNavController().navigate(R.id.action_updateTareaFragment_to_videoFragment, bundle)
        }

        binding.btnAudioT.setOnClickListener {
            it.findNavController().navigate(R.id.action_updateTareaFragment_to_audio, bundle)
        }

        binding.multimediaTask.setOnClickListener {
            it.findNavController().navigate(R.id.action_updateTareaFragment_to_viewMultimediaFragment, bundle)
        }
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

    private fun saveTarea(){
        val title = binding.etTareaTitleUpdate.text.toString().trim()
        val subTitle = binding.etTareaSubTitleUpdate.text.toString().trim()
        val date = binding.tvTareaDateUpdate.text.toString().trim()
        val body = binding.etTareaBodyUpdate.text.toString().trim()

        if (title.isNotEmpty()) {
            scheduleNotificaction(title)
            val tarea = Tarea(currentTarea.id, title, subTitle, date, body)
            tareaViewModel.actualizarTarea(tarea)

            view?.findNavController()?.navigate(R.id.action_updateTareaFragment_to_homeFragment)

        } else {
            activity?.toast("Ingresa un Titulo")
        }
    }

    private fun deleteTarea() {
        AlertDialog.Builder(activity).apply {
            setTitle("Borrar nota")
            setMessage("¿Seguro que deseas eliminar la nota?")
            setPositiveButton("Eliminar") { _, _ ->
                tareaViewModel.borrarTarea(currentTarea)
                view?.findNavController()?.navigate(
                    R.id.action_updateTareaFragment_to_homeFragment
                )
            }
            setNegativeButton("Cancelar", null)
        }.create().show()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_actualizar_nota, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deleteTarea()
            }
            R.id.menu_save -> {
                saveTarea()
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