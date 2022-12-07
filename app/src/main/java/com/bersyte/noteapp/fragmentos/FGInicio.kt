package com.bersyte.noteapp.fragmentos

import android.os.Bundle
import android.view.*
import android.widget.Switch
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bersyte.noteapp.MainActivity
import com.bersyte.noteapp.R
import com.bersyte.noteapp.adaptador.AdaptadorNotas
import com.bersyte.noteapp.adaptador.AdaptadorTareas
import com.bersyte.noteapp.databinding.FgInicioBinding
import com.bersyte.noteapp.toast
import com.bersyte.noteapp.viewmodel.NoteViewModel
import com.bersyte.noteapp.viewmodel.TareaViewModel


class FGInicio : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FgInicioBinding? = null
    private val binding
        get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var taskViewModel: TareaViewModel
    private lateinit var taskAdapter: AdaptadorTareas
    private lateinit var noteAdapter: AdaptadorNotas


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FgInicioBinding.inflate(inflater, container, false)

        noteViewModel = (activity as MainActivity).noteViewModel
        taskViewModel = (activity as MainActivity).tareaViewModel

        return binding.root
    }

    private fun setUpRecyclerView() {
        noteAdapter = AdaptadorNotas()
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = noteAdapter
            noteViewModel.getAllNote().observe(viewLifecycleOwner) { notes ->
                noteAdapter.differ.submitList(notes)
                notes.updateUI()
            }
        }
    }

    private fun setUpRecyclerView2() {
        taskAdapter = AdaptadorTareas()
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
            setHasFixedSize(true)
            adapter = taskAdapter
            taskViewModel.getAllTarea().observe(viewLifecycleOwner) { tareas ->
                taskAdapter.differ.submitList(tareas)
                tareas.updateUI()
            }
        }
    }

    lateinit var aux:MenuItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(!binding.switch2.isChecked) setUpRecyclerView2()
        binding.switch2.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked){
                setUpRecyclerView()
            }
            else if(!isChecked){
                setUpRecyclerView2()
            }
        }
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddNote.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_newNoteFragment)
        }

        binding.fabAddTask.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_newTareaFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_inicio, menu)

        val searchItem = menu.findItem(R.id.menu_search).actionView as SearchView
        searchItem.isSubmitButtonEnabled = false
        searchItem.setOnQueryTextListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun <E> List<E>.updateUI() {
        if (isNotEmpty()) {
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.GONE
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchNote(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            searchNote(newText)
        }
        return true
    }

    private fun searchNote(query: String?) {
        if(binding.switch2.isChecked){
            val searchQuery = "%$query%"
            noteViewModel.buscarNota(searchQuery).observe(this) { notes ->
                noteAdapter.differ.submitList(notes)
            }
        }
        else{
            val searchQuery = "%$query%"
            taskViewModel.buscarTarea(searchQuery).observe(this) { tareas ->
                taskAdapter.differ.submitList(tareas)
            }
        }

    }
}



