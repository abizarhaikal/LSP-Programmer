package com.example.lspprogrammer.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lspprogrammer.databinding.FragmentLoginBinding
import com.example.lspprogrammer.ui.administrator.AdminActivity
import com.example.lspprogrammer.ui.user.UserActivity
import com.example.lspprogrammer.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val loginViewModel: LoginViewModel by viewModel()
    private val firebaseAuth  = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener { saveData() }
        loginViewModel.getSession().observe(requireActivity()) { session ->
            if (session.isLogin && session.role == "Admin") {
                activity?.finish()
                startActivity(Intent(requireContext(), AdminActivity::class.java))
            } else if (session.isLogin && session.role == "User") {
                activity?.finish()
                startActivity(Intent(requireContext(), UserActivity::class.java))
            }
        }

        binding.tvCreateAccount.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
    }

    private fun saveData() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val phone = binding.edtPhone.text.toString()
        val password = binding.edtPassword.text.toString()

        if (email.isEmpty()) {
            binding.edtEmail.error = "Email harus diisi"
            return
        }
        if (password.isEmpty()) {
            binding.edtPassword.error = "Password harus diisi"
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid ?: ""
                    getUserRole(userId)
                } else {
                    Toast.makeText(requireContext(), "Login gagal", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(requireContext(), "Login gagal", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToPreference(id: String, role: String) {
        val email = binding.edtEmail.text.toString()
        val name = binding.edtName.text.toString()
        val isLogin = true
        loginViewModel.saveUser(email, id, name, isLogin, role)
    }

    private fun getUserRole(userId: String) {
        firestore.collection("data_user").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    role?.let {
                        navigationToLayoutBasedOnRole(it)
                        saveUserToPreference(userId, role)
                    }
                } else {
                    Log.d("GET DATA ROLE", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e("GET DATA ROLE", "Error getting documents: ", e)
            }
    }

    private fun navigationToLayoutBasedOnRole(role: String) {
        val intent = when (role) {
            "Admin" -> Intent(activity, AdminActivity::class.java)
            "User" -> Intent(activity, UserActivity::class.java)
            else -> return
        }
        startActivity(intent)
        activity?.finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
