  package com.bersyte.noteapp.fragmentos

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R
import com.bersyte.noteapp.adaptador.AdaptadorNotas
import com.bersyte.noteapp.adaptador.AdaptadorTareas
import com.bersyte.noteapp.databinding.ActivityMainBinding.inflate
import com.bersyte.noteapp.databinding.FgInicioBinding
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.model.Tarea
import com.bersyte.noteapp.viewmodel.NoteViewModel
import com.bersyte.noteapp.viewmodel.TareaViewModel
import com.google.android.material.snackbar.Snackbar


  class FGInicio : Fragment(R.layout.fg_inicio),
    SearchView.OnQueryTextListener {
      private var _binding: FgInicioBinding? = null
      private val binding get() = _binding!!
      private lateinit var notesViewModel: NoteViewModel
      private lateinit var adaptadorNotas: AdaptadorNotas
      private lateinit var tareasViewModel: TareaViewModel
      private lateinit var adaptadorTareas: AdaptadorTareas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FgInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        tareasViewModel = (activity as MainActivity).tareaViewModel

        binding.switch2.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                setUpRecyclerView()
            }
            else if(!isChecked){
                setUpRecyclerView2()
            }
        }

        binding.fabAddNote.setOnClickListener {
            mostrarPopUp(view)
        }
    }


    //crear funcion para mostrar el PopUp
    fun mostrarPopUp(v : View){
        this.context?.let {
            PopupMenu(it, v).apply {
                inflate(R.menu.menu_emergente)
                setOnMenuItemClickListener {
                    when(it!!.itemId){
                        R.id.agregarNota -> {
                            v.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
                            true
                        }
                        R.id.agregarTarea -> {
                            v.findNavController().navigate(R.id.action_homeFragment_to_newTareaFragment)
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }
    }

    private fun setUpRecyclerView() {
        adaptadorNotas = AdaptadorNotas()

        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = adaptadorNotas
        }

        activity?.let {
            notesViewModel.getAllNote().observe(viewLifecycleOwner, { note ->
                adaptadorNotas.differ.submitList(note)
                updateUI(note)
            })

        }
    }

      private fun setUpRecyclerView2() {
          adaptadorTareas = AdaptadorTareas()

          binding.recyclerView.apply {
              layoutManager = StaggeredGridLayoutManager(
                  2,
                  StaggeredGridLayoutManager.VERTICAL
              )
              setHasFixedSize(true)
              adapter = adaptadorTareas
          }

          activity?.let {
              tareasViewModel.getAllTarea().observe(viewLifecycleOwner, { tarea ->
                  adaptadorTareas.differ.submitList(tarea)
                  updateUI2(tarea)
              })

          }
      }

    private fun updateUI(note: List<Note>) {
        if (note.isNotEmpty()) {
            binding.cardView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.cardView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

      private fun updateUI2(tarea: List<Tarea>) {
          if (tarea.isNotEmpty()) {
              binding.cardView.visibility = View.GONE
              binding.recyclerView.visibility = View.VISIBLE
          } else {
              binding.cardView.visibility = View.VISIBLE
              binding.recyclerView.visibility = View.GONE
          }
      }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_inicio, menu)
        val mMenuSearch = menu.findItem(R.id.menu_search).actionView as SearchView
        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchNote(query)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        if (newText != null) {
            searchNote(newText)
        }
        return true
    }


    private fun searchNote(query: String?) {
        val searchQuery = "%$query%"
        notesViewModel.buscarNota(searchQuery).observe(
            this, { list ->
                adaptadorNotas.differ.submitList(list)
            }
        )

        val searchQuery2 = "%$query%"
        tareasViewModel.buscarTarea(searchQuery).observe(
            this, { list ->
                adaptadorTareas.differ.submitList(list)
            }
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}