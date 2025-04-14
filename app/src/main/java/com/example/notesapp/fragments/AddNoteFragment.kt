package com.example.notesapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.MainActivity
import com.example.notesapp.R
import com.example.notesapp.adapter.NoteAdapter
import com.example.notesapp.databinding.FragmentAddNoteBinding
import com.example.notesapp.databinding.FragmentHomeBinding
import com.example.notesapp.model.Note
import com.example.notesapp.viewmodel.NoteViewModel


class AddNoteFragment : Fragment(R.layout.fragment_add_note), MenuProvider {

    private var addNoteBinding : FragmentAddNoteBinding?= null
    private val binding get() = addNoteBinding!!

    private lateinit var notesViewModel: NoteViewModel
    private lateinit var addNoteView :View


    // Keys for saving state
    companion object {
        private const val NOTE_TITLE_KEY = "note_title"
        private const val NOTE_DESC_KEY = "note_desc"
    }

    //on create FragmentAddNote is Bound
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addNoteBinding = FragmentAddNoteBinding.inflate(inflater, container,false)
        return binding.root
    }

    //with help of viewmodel recycler view and  addNoteview is set up
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner,Lifecycle.State.RESUMED)

        notesViewModel = (activity as MainActivity).noteViewModel
        addNoteView = view
        //setUpHomeRecyclerView()

    }

    // Handling rotations and restore data
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current input
        outState.putString(NOTE_TITLE_KEY, binding.addNoteTitle.text.toString())
        outState.putString(NOTE_DESC_KEY, binding.addNoteDesc.text.toString())
    }

    //Note is saved and returned to home
    private fun saveNote(view: View){
        val noteTitle = binding.addNoteTitle.text.toString().trim()
        val noteDesc = binding.addNoteDesc.text.toString().trim()

        if(noteTitle.isNotEmpty()){
            val note = Note(0,noteTitle,noteDesc)
            notesViewModel.addNote(note)

            Toast.makeText(addNoteView.context,"Note Save",Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment,false)

        }else{
            Toast.makeText(addNoteView.context,"Please enter note Title",Toast.LENGTH_SHORT).show()
        }
    }

    //menu is inflated
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_note,menu)
    }

    //if save menu is selected it saves to add note view
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.saveMenu->{
                saveNote(addNoteView)
                true
            }
            else -> false
        }
    }

    //destroys if we go to previous screen
    override fun onDestroy() {
        super.onDestroy()
        addNoteBinding = null
    }


}