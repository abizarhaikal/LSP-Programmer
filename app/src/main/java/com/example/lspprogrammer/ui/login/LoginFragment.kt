package com.example.lspprogrammer.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.lspprogrammer.databinding.FragmentLoginBinding
import com.example.lspprogrammer.ui.administrator.AdminActivity
import com.example.lspprogrammer.ui.user.UserActivity
import com.example.lspprogrammer.viewmodel.LoginViewModel
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val loginViewModel: LoginViewModel by viewModel()
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
    }

    private fun saveData() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val role = binding.spinnerRole.selectedItem.toString()
        val phone = binding.edtPhone.text.toString()

        if (name.isEmpty() || email.isEmpty() || role == "Pilih Role" || phone.isEmpty()) {
            binding.edtName.error = "Nama tidak boleh kosong"
            binding.edtEmail.error = "Email tidak boleh kosong"
            binding.edtPhone.error = "Nomor Telepon tidak boleh kosong"
            binding.spinnerRole.setSelection(0)
        } else {
            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "role" to role
            )

            val userRef = firestore.collection("data_user").document()
            userRef.set(userData)
                .addOnSuccessListener {
                    saveUserToPreference(userRef.id)
                    binding.edtName.text?.clear()
                    binding.edtEmail.text?.clear()
                    binding.edtPhone.text?.clear()
                    binding.spinnerRole.setSelection(0)
                    showToast("Data User Berhasil Dibuat")
                    getUserRole(userRef.id)
                }
                .addOnFailureListener { e ->
                    showToast("Data Error ${e.message}")
                }
        }
    }

    private fun saveUserToPreference(id: String) {
        val email = binding.edtEmail.text.toString()
        val name = binding.edtName.text.toString()
        val role = binding.spinnerRole.selectedItem.toString()
        val isLogin = true
        loginViewModel.saveUser(email, id, name, isLogin, role)
    }

    private fun getUserRole(userId: String) {
        firestore.collection("data_user").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val role = document.getString("role")
                    role?.let {
                        navigationToLayoutBasedOnRole(it)
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
        Log.d("Role", "User Role: $role")
        if (role == "Admin") {
            val intent = Intent(activity, AdminActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } else if (role == "User") {
            val intent = Intent(activity, UserActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
