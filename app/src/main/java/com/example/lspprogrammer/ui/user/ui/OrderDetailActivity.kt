package com.example.lspprogrammer.ui.user.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lspprogrammer.adapter.OrderAdapter
import com.example.lspprogrammer.databinding.ActivityOrderDetailBinding
import com.example.lspprogrammer.model.OrderItem
import com.example.lspprogrammer.viewmodel.OrderViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailBinding
    private val firestore = FirebaseFirestore.getInstance()

    private val orderViewModel: OrderViewModel by viewModel()
    private lateinit var orderAdapter: OrderAdapter

    var orderId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val orderId = intent.getStringExtra("orderId") ?: ""
        setupRecyclerView()
        if (orderId.isNotEmpty()) {
            getDataOrderById(orderId)
            binding.btnSelesai.visibility = View.VISIBLE
        } else if (orderId.isEmpty()) {
            getUserId()
            binding.btnSelesai.visibility = View.GONE
        }

        binding.btnSelesai.setOnClickListener {
            updateStatus(orderId)
        }
    }

    private fun updateStatus(orderId: String) {
        val orderRef = firestore.collection("orders").document(orderId)
        orderRef
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    orderRef.update("status", "Selesai")
                        .addOnSuccessListener {
                            Toast.makeText(this, "Order selesai", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView() {
        binding.rvOrderDetail.layoutManager = LinearLayoutManager(this)
        orderAdapter = OrderAdapter(emptyList())
        binding.rvOrderDetail.adapter = orderAdapter
    }

    private fun getUserId() {
        orderViewModel.getSession().observe(this) { session ->
            if (!session.isLogin) {
                finish()
            } else {
                getDataOrder(session.userId)
            }
        }
    }

    private fun getDataOrderById(orderId: String) {
        firestore.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    showOrderDetailsId(document)
                } else {
                    Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showOrderDetailsId(document: DocumentSnapshot) {
        val items = document.get("items") as? List<Map<String, Any>> ?: emptyList()
        val totalItems = document.get("totalItems")?.toString()?.toIntOrNull() ?: 0
        val totalPrice = document.get("totalPrice")?.toString()?.toIntOrNull() ?: 0
        val paymentMethod = document.getString("paymentMethod") ?: "Tidak Diketahui"
        val status = document.getString("status") ?: "Tidak Diketahui"

        val orderItems = items.mapNotNull { item ->
            try {
                OrderItem(
                    name = item["name"] as? String ?: "",
                    quantity = (item["quantity"] as? Number)?.toInt() ?: 0,
                    price = (item["price"] as? Number)?.toInt() ?: 0
                )
            } catch (e: Exception) {
                Log.e("OrderDetailActivity", "Error parsing item: $e")
                null
            }
        }

        orderAdapter.updateItems(orderItems)

        orderId = document.id
        binding.tvOrderId.text = orderId
        binding.tvHargaOrder.text = "Rp. $totalPrice"
        binding.tvMetodePembayaran.text = paymentMethod
        binding.chipStatus.text = status
        Log.d("OrderDetailActivity", "Order details loaded successfully")
    }

    private fun getDataOrder(userId: String) {
        if (userId.isNotEmpty()) {
            firestore.collection("orders")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        showOrderDetails(documents)
                    } else {
                        Log.d("OrderDetailActivity", "No order found for user: $userId")
                        Toast.makeText(this, "No order found for user: $userId", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("OrderDetailActivity", "Error fetching order data: $e")
                    Toast.makeText(this, "Error fetching order data: $e", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d("OrderDetailActivity", "User ID is empty")
            Toast.makeText(this, "User ID is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showOrderDetails(documents: QuerySnapshot) {
        val firstOrder = documents.documents.firstOrNull()
        firstOrder?.let { document ->
            showOrderDetailsId(document)
        } ?: run {
            Log.d("OrderDetailActivity", "No order details found")
            Toast.makeText(this, "No order details found", Toast.LENGTH_SHORT).show()
        }
    }
}
