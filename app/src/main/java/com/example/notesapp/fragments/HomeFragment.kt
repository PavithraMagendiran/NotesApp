package com.example.notesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.MainActivity
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.databinding.FragmentHomeBinding
import com.example.notesapp.model.Note
import com.example.notesapp.viewmodel.NoteViewModel


class HomeFragment: Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener, MenuProvider {

    //variables
    private var homeBinding : FragmentHomeBinding?= null
    private val binding get() = homeBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container,false)
        return binding.root
    }

    //with help of viewmodel, recycler view is setup and if addNoteFab is selected it will nav from home to addNote
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerForContextMenu(binding.homeRecyclerView)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        setUpHomeRecyclerView()

        binding.addNoteFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addNoteFragment)
        }

    }
    //update UI if note is added or edited
    private fun updateUI(note: List<Note>?){
        if(note != null){
            if (note.isNotEmpty()){

                binding.emptyNotesImage.visibility = View.GONE
                binding.homeRecyclerView.visibility = View.VISIBLE

            } else {

                binding.emptyNotesImage.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.GONE

            }
        }
    }
    //vertical Scroll with touch handling
    private fun setUpHomeRecyclerView() {
        noteAdapter = NoteAdapter()
        binding.homeRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = noteAdapter
        }

        activity?.let {
            notesViewModel.getAllNote().observe(viewLifecycleOwner) { note ->
                noteAdapter.differ.submitList(note)
                updateUI(note)
            }

        }
    }

    //search the note created
    private fun searchNote(query: String?){
        val searchQuery = "%${query ?: ""}%"

        notesViewModel.searchNote(searchQuery).observe(this){ list->
            noteAdapter.differ.submitList(list)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false

    }

    override fun onQueryTextChange(newText: String?): Boolean{
        if(newText != null){
            searchNote(newText)
        }
        return true
    }

    //destroys if we go to previous screen
    override fun onDestroy() {
        super.onDestroy()
        homeBinding = null
    }

    //inflate the home menu and perform search
    override fun onCreateMenu(menu : Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)

        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }

    //Long press on note to pop the context menu it will show edit and delete
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = noteAdapter.selectedPosition
        if (position != RecyclerView.NO_POSITION) {
            val note = noteAdapter.getNoteAt(position)

            //if edit is selected it will redirect to the EditnoteFragment
            when (item.itemId) {
                R.id.menu_edit -> {
                    val direction = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(note)
                    view?.findNavController()?.navigate(direction)
                    return true
                }
                R.id.menu_delete -> {
                    //Alert Dialog will pop if delete is selected
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delete Note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Delete") { _, _ ->
                            notesViewModel.deleteNote(note)
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                    return true
                }
            }
        }
        return super.onContextItemSelected(item)
    }


}