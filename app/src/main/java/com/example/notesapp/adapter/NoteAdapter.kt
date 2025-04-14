package com.example.notesapp.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.databinding.NoteLayoutBinding
import com.example.notesapp.fragments.HomeFragmentDirections
import com.example.notesapp.model.Note

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    //for providing views that represent items in a data set
    //class NoteViewHolder(val itemBinding : NoteLayoutBinding) : RecyclerView.ViewHolder(itemBinding.root)

    var onItemContextMenuClick: ((Note, Int) -> Unit)? = null
    var selectedPosition: Int = -1
    //val note = noteAdapter.getNoteAt(position)


    // to show context menu
    inner class NoteViewHolder(val itemBinding: NoteLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root),
        View.OnCreateContextMenuListener {

        init {
            itemView.setOnCreateContextMenuListener(this)
            itemView.setOnLongClickListener {
                selectedPosition = adapterPosition
                false
            }
        }

        fun bind(note: Note) {
            itemBinding.noteTitle.text = note.noteTitle
            itemBinding.noteDesc.text = note.noteDesc
        }

        //create context menu
        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu.setHeaderTitle("Select Action")
            menu.add(adapterPosition, R.id.menu_edit, 0, "Edit")
            menu.add(adapterPosition, R.id.menu_delete, 1, "Delete")
        }
    }


    // old data will be replaced by new data if it is same
    private val differCallback = object : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id &&
                    oldItem.noteDesc == newItem.noteDesc &&
                    oldItem.noteTitle == newItem.noteTitle
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem ==  newItem
        }

    }
    val differ = AsyncListDiffer(this,differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            NoteLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = differ.currentList[position]

        holder.itemBinding.noteTitle.text = currentNote.noteTitle
        holder.itemBinding.noteDesc.text = currentNote.noteDesc

        holder.itemView.setOnClickListener{
            val direction = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(currentNote)
            it.findNavController().navigate(direction)
        }

    }

    fun getNoteAt(position: Int): Note = differ.currentList[position]
}