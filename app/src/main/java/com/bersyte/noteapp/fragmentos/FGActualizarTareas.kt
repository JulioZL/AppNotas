package com.bersyte.noteapp.fragmentos

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R
import com.bersyte.noteapp.databinding.FgActualizarNotaBinding
import com.bersyte.noteapp.databinding.FgActualizarTareaBinding
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.model.Tarea
import com.bersyte.noteapp.toast
import com.bersyte.noteapp.viewmodel.NoteViewModel
import com.bersyte.noteapp.viewmodel.TareaViewModel
import java.text.SimpleDateFormat
import java.util.*


class FGActualizarTareas : Fragment(R.layout.fg_actualizar_tarea) {

    private var _binding: FgActualizarTareaBinding? = null
    private val binding get() = _binding!!

    private val args: FGActualizarTareasArgs by navArgs()
    private lateinit var currentTarea: Tarea
    private lateinit var tareaViewModel: TareaViewModel

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

        binding.fabDone.setOnClickListener {
            val title = binding.etTareaTitleUpdate.text.toString().trim()
            val subTitle = binding.etTareaSubTitleUpdate.text.toString().trim()
            val date = binding.tvTareaDateUpdate.text.toString().trim()
            val body = binding.etTareaBodyUpdate.text.toString().trim()

            if (title.isNotEmpty()) {
                val tarea = Tarea(currentTarea.id, title, subTitle, date, body)
                tareaViewModel.actualizarTarea(tarea)

                view.findNavController().navigate(R.id.action_updateTareaFragment_to_homeFragment)

            } else {
                activity?.toast("Ingresa un Titulo")
            }
        }
    }

    private fun deleteNote() {
        AlertDialog.Builder(activity).apply {
            setTitle("Borrar nota")
            setMessage("¿Seguro que deseas eliminar la nota?")
            setPositiveButton("Eliminar") { _, _ ->
                tareaViewModel.borrarTarea(currentTarea)
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

}