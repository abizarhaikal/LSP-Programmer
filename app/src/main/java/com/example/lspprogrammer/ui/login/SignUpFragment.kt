package com.example.lspprogrammer.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lspprogrammer.R
import com.example.lspprogrammer.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignUpFragment : Fragment() {

    private lateinit var binding : FragmentSignUpBinding
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCreateAccount.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.btnLogin.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val nama = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val phone = binding.edtPhone.text.toString()
        val role = binding.spinnerRole.selectedItem.toString()

        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || role.isEmpty()) {
            binding.edtName.error = "Nama harus diisi"
            binding.edtEmail.error = "Email harus diisi"
            binding.edtPassword.error = "Password harus diisi"
            binding.edtPhone.error = "Nomor telepon harus diisi"
            return
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = firebaseAuth.currentUser

                        val userId = firebaseUser?.uid ?: ""

                        val userMap = mapOf(
                            "userId" to userId,
                            "name" to nama,
                            "email" to email,
                            "password" to password,
                            "phone" to phone,
                            "role" to role
                        )
                        firestore.collection("data_user").document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                val action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment()
                                findNavController().navigate(action)
                            }
                            .addOnFailureListener { e ->
                                e.printStackTrace()
                            }
                    } else {
                        task.exception.let {
                            it?.printStackTrace()
                        }
                    }
                }
                .addOnFailureListener { e->
                    e.printStackTrace()
                }
        }
    }


}