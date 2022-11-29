package com.bersyte.noteapp.fragmentos

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R
import com.bersyte.noteapp.databinding.FgAgregarTareaBinding
import com.bersyte.noteapp.model.Tarea
import com.bersyte.noteapp.toast
import com.bersyte.noteapp.viewmodel.TareaViewModel
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
}