package com.metehanbolat.kotlinnoteviewbinding.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.metehanbolat.kotlinnoteviewbinding.adapter.NoteRecyclerAdapter
import com.metehanbolat.kotlinnoteviewbinding.databinding.FragmentNotesBinding
import com.metehanbolat.kotlinnoteviewbinding.model.Note

class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var noteArrayList: ArrayList<Note>
    private lateinit var noteAdapter: NoteRecyclerAdapter

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth
        db = Firebase.firestore

        noteArrayList = ArrayList()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        noteAdapter = NoteRecyclerAdapter(noteArrayList)
        binding.recyclerView.adapter = noteAdapter


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getData(){
        val collection = db.collection(auth.currentUser?.email.toString()).orderBy("date", Query.Direction.DESCENDING)
        collection.get().addOnSuccessListener { doc ->
            if (doc != null){
                if (!doc.isEmpty){
                    val documents = doc.documents
                    noteArrayList.clear()
                    for (document in documents){
                        val title = document.get("noteTitle") as String
                        val note = document.get("note") as String
                        val downloadUrl = document.get("downloadUrl") as String
                        val notes = Note(title, note, downloadUrl)
                        noteArrayList.add(notes)
                    }
                    noteAdapter.notifyDataSetChanged()
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(),e.localizedMessage,Toast.LENGTH_SHORT).show()
        }
    }
}