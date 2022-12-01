package com.bersyte.noteapp.fragmentos

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R
import com.bersyte.noteapp.databinding.FgActualizarNotaBinding
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.toast
import com.bersyte.noteapp.viewmodel.NoteViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class FGActualizarNotas : Fragment(R.layout.fg_actualizar_nota) {

    private var _binding: FgActualizarNotaBinding? = null
    private val binding get() = _binding!!

    private val args: FGActualizarNotasArgs by navArgs()
    private lateinit var currentNote: Note
    private lateinit var noteViewModel: NoteViewModel

    val REQUEST_IMAGE_CAPTURE  = 10
    lateinit var currentVideoPath: String
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
        _binding = FgActualizarNotaBinding.inflate(
            inflater,
            container,
            false
        )
        //revisar
        //noteViewModel=(activity as MainActivity).noteViewModel
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!

        var currentDate:String? = null
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        currentDate = sdf.format(Date())

        binding.etNoteBodyUpdate.setText(currentNote.noteBody)
        binding.tvNoteDateUpdate.setText(currentDate)
        binding.etNoteSubTitleUpdate.setText(currentNote.noteSubTitle)
        binding.etNoteTitleUpdate.setText(currentNote.noteTitle)

        //validacion para pintar imagen y video
        if(currentNote.noteImagen != ""){
            photoURI = currentNote.noteImagen.toUri()
            binding.enoteImagenUpdate.setImageURI(photoURI)
            binding.enoteImagenUpdate.visibility = View.VISIBLE
        }

        if(currentNote.noteVideo != ""){
            videoURI = currentNote.noteVideo.toUri()
            binding.eNoteVideoUpdate.setVideoURI(videoURI)
            binding.eNoteVideoUpdate.visibility = View.VISIBLE
        }

        binding.fabDone.setOnClickListener {
            val title = binding.etNoteTitleUpdate.text.toString().trim()
            val subTitle = binding.etNoteSubTitleUpdate.text.toString().trim()
            val date = binding.tvNoteDateUpdate.text.toString().trim()
            val body = binding.etNoteBodyUpdate.text.toString().trim()

            var imagen = ""
            var video = ""

            if (photoURI != null){
                imagen = photoURI.toString()
            }

            if(videoURI != null){
                video = videoURI.toString()
            }

            if (title.isNotEmpty()) {
                val note = Note(currentNote.id, title, subTitle, date, body, imagen, video,"")
                noteViewModel.actualizarNota(note)

                view.findNavController().navigate(R.id.action_updateNoteFragment_to_homeFragment)

            } else {
                activity?.toast("Ingresa un Titulo")
            }
        }
        binding.enoteImagenUpdate.setOnLongClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(R.string.alerta_borrarIMG_titulo)
                .setMessage(R.string.alerta_borrarIMG_mensaje)
                .setNegativeButton(R.string.cancelar) { view, _ ->
                    photoURI = null
                    binding.enoteImagenUpdate.visibility = View.GONE
                    view.dismiss()
                }
                .setPositiveButton(R.string.aceptar) { view, _ ->
                    Toast.makeText(requireContext(), "Se ha eliminado", Toast.LENGTH_SHORT).show()
                    view.dismiss()
                }
                .setCancelable(false)
                .create()

            dialog.show()
            return@setOnLongClickListener false
        }

        binding.eNoteVideoUpdate.setOnClickListener{
            binding.eNoteVideoUpdate.start()
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Borrar nota")
            setMessage("¿Seguro que deseas eliminar la nota?")
            setPositiveButton("Eliminar") { _, _ ->
                noteViewModel.borrarNota(currentNote)
                view?.findNavController()?.navigate(
                    R.id.action_updateNoteFragment_to_homeFragment
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
                deleteNote()
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
            binding.enoteImagenUpdate.setImageURI(
                photoURI
            )
        }
    }

    lateinit var currentPhotoPath: String

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
}