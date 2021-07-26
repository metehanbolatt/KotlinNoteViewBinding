package com.metehanbolat.kotlinnoteviewbinding.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.metehanbolat.kotlinnoteviewbinding.R
import com.metehanbolat.kotlinnoteviewbinding.databinding.ActivityNoteBinding
import com.metehanbolat.kotlinnoteviewbinding.user.MainActivity

class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        initBottomLayout()

    }

    private fun initBottomLayout(){
        val layoutBottom = findViewById<LinearLayout>(R.id.layoutBottom)
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutBottom)
        layoutBottom.findViewById<TextView>(R.id.textBottom).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        layoutBottom.findViewById<Button>(R.id.buttonLogOut).setOnClickListener {
            auth.signOut()
            val intent = Intent(baseContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        layoutBottom.findViewById<FloatingActionButton>(R.id.uploadButton).setOnClickListener {
            navController = findNavController(R.id.fragmentContainerView2)
            if (navController.currentDestination.toString() != "Destination(com.metehanbolat.kotlinnoteviewbinding:id/noteUploadFragment) label=fragment_note_upload class=com.metehanbolat.kotlinnoteviewbinding.notes.NoteUploadFragment"){
                navController.navigate(R.id.action_notesFragment_to_noteUploadFragment)
            }else{
                Toast.makeText(baseContext,"You are already on Upload View",Toast.LENGTH_SHORT).show()
            }
        }
    }
}