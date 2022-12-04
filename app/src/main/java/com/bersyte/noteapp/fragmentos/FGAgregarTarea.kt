package com.bersyte.noteapp.fragmentos

import android.annotation.SuppressLint
import android.app.*
import android.app.Notification
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bersyte.noteapp.*
import com.bersyte.noteapp.databinding.FgAgregarTareaBinding
import com.bersyte.noteapp.db.TareaDatabase
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

    //fecha
    var currentDate: String? = null

    //Variables para video e imagen
    val REQUEST_IMAGE_CAPTURE = 10
    val REQUEST_VIDEO_CAPTURE = 20

    lateinit var currentVideoPath: String
    lateinit var currentPhotoPath: String
    var photoURI: Uri? = null
    var videoURI: Uri? = null

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

        binding.fabImg.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager).also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File

                        null
                    }

                    // Continue only if the File was successfully created
                    photoFile?.also {
                        photoURI = FileProvider.getUriForFile(
                            requireContext(),
                            "com.example.noteeapp.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }

        binding.fabVideo.setOnClickListener {
            Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
                takeVideoIntent.resolveActivity(requireActivity().packageManager).also {

                    // Create the File where the photo should go
                    val videoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File

                        null
                    }

                    // Continue only if the File was successfully created
                    videoFile?.also {
                        videoURI = FileProvider.getUriForFile(
                            requireContext(),
                            "com.example.noteeapp.fileprovider",
                            it
                        )
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
                    }
                }
            }
        }

        binding.eNoteVideo.setOnClickListener {
            configureVideoView()
        }

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

        return binding.root
    }

    private fun showTimePikerDialog() {
        val newFragment = TimePicker { onTimeSelected(it) }
        activity?.let { newFragment.show(it.supportFragmentManager, "timePicker") }
    }

    private fun onTimeSelected(time: String) {
        hora.setText(time)
    }

    private fun showDatePickerDialog() {
        val newFragment = DatePicker { day, month, year -> onDateSelected(day, month, year) }
        activity?.let { newFragment.show(it.supportFragmentManager, "datePicker") }
    }

    @SuppressLint("SetTextI18n")
    private fun onDateSelected(day: Int, month: Int, year: Int) {
        fecha.setText("$day/$month/$year")
    }

    private var mediaController: MediaController? = null
    private fun configureVideoView() {
        binding.eNoteVideo.setVideoPath(currentVideoPath)
        mediaController = MediaController(context)
        mediaController?.setAnchorView(binding.eNoteVideo)
        binding.eNoteVideo.setMediaController(mediaController)
        binding.eNoteVideo.start()
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        //val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val storageDir: File? = activity?.filesDir
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            currentVideoPath = absolutePath
        }
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

    // Notification
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleNotificaction(titulo: String) {
        val intent = Intent(context, MiReceiverParaAlarma::class.java)
        val message = "Tienes esta tarea pendiente"
        intent.putExtra(titleExtra, titulo)
        intent.putExtra(messageExtra, message)

        val alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            0
        )

        // val time = getTime()
        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            //time,
            SystemClock.elapsedRealtime() + 10 * 1000,
            pendingIntent
        )
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat =
            android.text.format.DateFormat.getLongDateFormat(requireContext().applicationContext)
        val timeFormat =
            android.text.format.DateFormat.getTimeFormat(requireContext().applicationContext)

        AlertDialog.Builder(this@FGAgregarTarea.requireContext())
            .setTitle("Notification Scheduled")
            .setMessage(
                "Title: " + title +
                        "\nMessage: " + message +
                        "\nAt: " + dateFormat.format(date) + " " + timeFormat.format(date)
            )
            .setPositiveButton("Okay") { _, _ -> }
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            binding.enoteImagen.setImageURI(photoURI)
            binding.enoteImagen.visibility = View.VISIBLE
        }

        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            binding.eNoteVideo.setVideoURI(videoURI)
            binding.eNoteVideo.visibility = View.VISIBLE
        }
    }
}