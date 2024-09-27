package com.example.lspprogrammer.ui.user.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lspprogrammer.adapter.StrukAdapter
import com.example.lspprogrammer.databinding.FragmentStrukBinding
import com.example.lspprogrammer.model.DataStruk
import com.example.lspprogrammer.model.OrderItems
import com.example.lspprogrammer.viewmodel.StrukViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.koin.androidx.viewmodel.ext.android.viewModel


class StrukFragment : Fragment() {

    private lateinit var binding: FragmentStrukBinding
    private lateinit var adapter: StrukAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val strukList = mutableListOf<DataStruk>()
    private val strukViewModel: StrukViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStrukBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StrukAdapter(strukList) { dataStruk ->
            val intent = Intent(requireContext(), OrderDetailActivity::class.java).apply {
                putExtra("orderId", dataStruk.orderId)
            }
            startActivity(intent)
        }
        binding.rvStruk.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStruk.adapter = adapter

        strukViewModel.getSession().observe(viewLifecycleOwner) { session ->
            getDataStruk(session.userId)

        }
    }

    private fun getDataStruk(userId: String) {
        firestore.collection("orders")
            .orderBy("timestamp",Query.Direction.DESCENDING)
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val orderId = document.id
                    val status = document.getString("status") ?: ""
                    val totalPrice = document.getLong("totalPrice")?.toInt() ?: 0
                    val itemsData = document.get("items") as? List<Map<String, Any>> ?: emptyList()

                    val paymentMethod = document.getString("paymentMethod") ?: ""
                    val items = itemsData.map { item ->
                        OrderItems(
                            id = item["id"] as String,
                            name = item["name"] as String,
                            price = (item["price"] as Long).toInt().toString(),
                            quantity = (item["quantity"] as Long).toInt().toString()
                        )
                    }

                    // Debugging timestamp
                    val timeStamp = document.getTimestamp("timestamp")
                    if (timeStamp == null) {
                        Toast.makeText(requireContext(), "Timestamp is null for order $orderId", Toast.LENGTH_SHORT).show()
                    } else {
                        val dataStruk = DataStruk(
                            orderId = orderId,
                            timeStamp = timeStamp,
                            status = status,
                            items = items,
                            totalPrice = totalPrice,
                            paymentMethod = paymentMethod
                        )
                        strukList.add(dataStruk)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}