package com.bersyte.noteapp.fragmentos

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R
import com.bersyte.noteapp.databinding.FgAgregarNotaBinding
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.toast
import com.bersyte.noteapp.viewmodel.NoteViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


class FGAgregarNota : Fragment(R.layout.fg_agregar_nota) {

    private var _binding: FgAgregarNotaBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var mView: View

    //fecha
    var currentDate:String? = null

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

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

        currentDate = sdf.format(Date())
        binding.tvDate.text = currentDate

        var id=-1
        val bundle = Bundle()
        //id = arguments?.getString("id")!!.toInt()

        bundle.putString("id", id.toString())
        binding.btnFotoT.setOnClickListener {
            it.findNavController().navigate(R.id.action_newNoteFragment_to_photoFragment, bundle)
        }

        binding.btnVideoT.setOnClickListener {
            it.findNavController().navigate(R.id.action_newNoteFragment_to_videoFragment, bundle)
        }

        binding.btnAudioT.setOnClickListener {
            it.findNavController().navigate(R.id.action_newNoteFragment_to_audio, bundle)
        }

        return binding.root
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
        if (noteTitle.isNotEmpty()) {
            val note = Note(0, noteTitle, noteSubTitle, notetvDate, noteBody)
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
}