package com.example.lspprogrammer.ui.administrator.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lspprogrammer.MainActivity
import com.example.lspprogrammer.adapter.ReportAdapter
import com.example.lspprogrammer.databinding.FragmentReportBinding
import com.example.lspprogrammer.model.Laporan
import com.example.lspprogrammer.model.LaporanItems
import com.example.lspprogrammer.viewmodel.ReportViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportFragment : Fragment() {

    private lateinit var binding: FragmentReportBinding

    private val reportList = mutableListOf<Laporan>()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var adapter: ReportAdapter

    private val reportViewModel: ReportViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReportAdapter(reportList)

        binding.rvReport.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReport.adapter = adapter
        getDataReport()

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Logout")
            setMessage("Apakah anda yakin ingin logout?")
            setPositiveButton("Yes") { _, _ ->
                reportViewModel.logout()
                activity?.finish()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            show()
        }
    }

    private fun getDataReport() {
        firestore.collection("orders")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val orderId = document.id
                    val status = document.getString("status") ?: 0
                    val totalPrice = document.getLong("totalPrice")?.toInt() ?: 0
                    val itemsData = document.get("items") as? List<Map<String, Any>> ?: emptyList()

                    val paymentMethod = document.getString("paymentMethod") ?: ""

                    val items = itemsData.map { item ->
                        LaporanItems(
                            id = item["id"] as String,
                            name = item["name"] as String,
                            price = (item["price"] as Long).toInt().toString(),
                            quantity = (item["quantity"] as Long).toInt().toString()
                        )
                    }
                    val timeStamp = document.getTimestamp("timestamp")
                    val dataLaporan = Laporan(
                        orderId = orderId,
                        timestamp = timeStamp!!,
                        status = status.toString(),
                        items = items,
                        totalPrice = totalPrice,
                        paymentMethod = paymentMethod
                    )
                    reportList.add(dataLaporan)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}