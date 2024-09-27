package com.example.lspprogrammer.ui.user.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.lspprogrammer.MainActivity
import com.example.lspprogrammer.R
import com.example.lspprogrammer.databinding.FragmentProfileBinding
import com.example.lspprogrammer.viewmodel.ProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val profileViewModel: ProfileViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        profileViewModel.getSession().observe(viewLifecycleOwner){ session ->
            if (!session.isLogin) {
                activity?.finish()
            } else {
                Log.d("ProfileFragment", "Session Data: Name = ${session.name}, Email = ${session.email}")
                binding.tvNama.text = session.name
                binding.tvEmail.text = session.email

            }
        }

        Glide.with(requireContext())
            .load(R.drawable.profile)
            .circleCrop()
            .into(binding.ivProfile)

        binding.btnLogout.setOnClickListener {
            logout()
        }

    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Logout")
            setMessage("Apakah anda yakin ingin logout?")
            setPositiveButton("Yes") { _, _ ->
                profileViewModel.logout()
                activity?.finish()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            show()
        }
    }


}