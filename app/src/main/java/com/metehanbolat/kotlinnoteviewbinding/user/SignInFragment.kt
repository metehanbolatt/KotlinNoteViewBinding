package com.metehanbolat.kotlinnoteviewbinding.user

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.metehanbolat.kotlinnoteviewbinding.R
import com.metehanbolat.kotlinnoteviewbinding.databinding.FragmentSignInBinding
import com.metehanbolat.kotlinnoteviewbinding.notes.NoteActivity

class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInSecondText.setOnClickListener {
            navController = view.findNavController()
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        binding.signInButton.setOnClickListener {
            val email = binding.signInEmail.text.toString()
            val password = binding.signInPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()){
                Toast.makeText(requireContext(),"Email or Password is empty.", Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    val intent = Intent(requireContext(),NoteActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(),it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
         _binding = null
    }
}