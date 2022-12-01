package com.bersyte.noteapp.fragmentos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R
import com.bersyte.noteapp.databinding.FgAgregarNotaBinding
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.toast
import com.bersyte.noteapp.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class FGAgregarNota : Fragment(R.layout.fg_agregar_nota) {

    private var _binding: FgAgregarNotaBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var mView: View

    //fecha
    var currentDate:String? = null

    //Variables para video e imagen
    val REQUEST_IMAGE_CAPTURE = 10
    val REQUEST_VIDEO_CAPTURE = 20

    lateinit var currentVideoPath: String
    lateinit var currentPhotoPath: String
    var photoURI: Uri? = null
    var videoURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FgAgregarNotaBinding.inflate(
            inflater,
            container,
            false
        )

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

        binding.eNoteVideo.setOnClickListener{
            binding.eNoteVideo.start()
        }

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

        currentDate = sdf.format(Date())
        binding.tvDate.text = currentDate

        return binding.root
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
        noteViewModel = (activity as MainActivity).noteViewModel
        mView = view
    }

    private fun saveNote(view: View) {
        val noteTitle = binding.etNoteTitle.text.toString().trim()
        val noteSubTitle = binding.etNoteSubTitle.text.toString().trim()
        val notetvDate = binding.tvDate.text.toString().trim()
        val noteBody = binding.etNoteBody.text.toString().trim()
        var imagen = ""
        var video = ""

        if(photoURI != null){
            imagen = photoURI.toString()
        }

        if(videoURI != null){
            video = videoURI.toString()
        }

        if (noteTitle.isNotEmpty()) {
            val note = Note(0, noteTitle, noteSubTitle, notetvDate, noteBody, imagen, video,"")
            noteViewModel.agregarNota(note)
            Snackbar.make(
                view, "Nota guardada.",
                Snackbar.LENGTH_SHORT
            ).show()
            view.findNavController().navigate(R.id.action_newNoteFragment_to_homeFragment)
        } else {
            activity?.toast("Ingresa un Titulo")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_agregar_nota, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_save -> {
                saveNote(mView)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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