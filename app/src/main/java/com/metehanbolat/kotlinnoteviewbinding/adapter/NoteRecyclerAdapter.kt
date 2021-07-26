package com.metehanbolat.kotlinnoteviewbinding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.metehanbolat.kotlinnoteviewbinding.databinding.RecyclerNoteRowBinding
import com.metehanbolat.kotlinnoteviewbinding.model.Note
import com.squareup.picasso.Picasso

class NoteRecyclerAdapter (private val noteList: ArrayList<Note>): RecyclerView.Adapter<NoteRecyclerAdapter.NoteHolder>() {

    class NoteHolder (val binding: RecyclerNoteRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val binding = RecyclerNoteRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {

        holder.binding.recyclerNoteTitle.text = noteList[position].noteTitle
        holder.binding.recyclerNoteImage
        Picasso.get().load(noteList[position].downloadUrl).into(holder.binding.recyclerNoteImage)
    }

    override fun getItemCount(): Int {
        return noteList.size
    }
}