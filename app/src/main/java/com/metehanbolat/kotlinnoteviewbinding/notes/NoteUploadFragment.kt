package com.metehanbolat.kotlinnoteviewbinding.notes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.metehanbolat.kotlinnoteviewbinding.R
import com.metehanbolat.kotlinnoteviewbinding.databinding.FragmentNoteUploadBinding
import java.util.*

class NoteUploadFragment : Fragment() {

    private var _binding: FragmentNoteUploadBinding? = null
    private val binding get() = _binding!!

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteUploadBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerLauncher()

        binding.noteUploadImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed for gallery!", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

        binding.noteUploadButton.setOnClickListener {
            val uuid = UUID.randomUUID()
            val imageName = "$uuid.jpg"
            val reference = storage.reference
            if (selectedPicture != null){
                val imageReference = reference.child(auth.currentUser?.email.toString()).child(imageName)
                imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                    val uploadPictureReference = storage.reference.child(auth.currentUser?.email.toString()).child(imageName)
                    uploadPictureReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()

                        if (auth.currentUser != null){
                            val noteMap = hashMapOf<String, Any>()
                            noteMap["downloadUrl"] = downloadUrl
                            noteMap["noteTitle"] = binding.noteUploadNoteTitle.text.toString()
                            noteMap["note"] = binding.noteUploadNote.text.toString()
                            noteMap["date"] = Timestamp.now()

                            firestore.collection(auth.currentUser?.email.toString()).add(noteMap).addOnSuccessListener {
                                navController = findNavController()
                                navController.navigate(R.id.action_noteUploadFragment_to_notesFragment)
                            }.addOnFailureListener { e ->
                                Toast.makeText(requireContext(),e.localizedMessage,Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }.addOnFailureListener{
                    Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_SHORT).show()
                }
            }else{

                val uploadPictureReference = storage.reference.child("default").child("default.png")
                uploadPictureReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()

                    if (auth.currentUser != null){
                        val noteMap = hashMapOf<String, Any>()
                        noteMap["downloadUrl"] = downloadUrl
                        noteMap["noteTitle"] = binding.noteUploadNoteTitle.text.toString()
                        noteMap["note"] = binding.noteUploadNote.text.toString()
                        noteMap["date"] = Timestamp.now()

                        firestore.collection(auth.currentUser?.email.toString()).add(noteMap).addOnSuccessListener {
                            navController = findNavController()
                            navController.navigate(R.id.action_noteUploadFragment_to_notesFragment)
                        }.addOnFailureListener { e ->
                            Toast.makeText(requireContext(),e.localizedMessage,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun registerLauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == Activity.RESULT_OK){
                val intentFromResult = result.data
                selectedPicture = intentFromResult?.data
                selectedPicture?.let {
                    binding.noteUploadImage.setImageURI(it)
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->

            if (result){
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(requireContext(),"Permission needed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}