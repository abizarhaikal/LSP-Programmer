package com.example.lspprogrammer.ui.user.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lspprogrammer.R
import com.example.lspprogrammer.adapter.PembayaranAdapter
import com.example.lspprogrammer.databinding.ActivityPaymentBinding
import com.example.lspprogrammer.model.DataMenu
import com.example.lspprogrammer.viewmodel.PaymentViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Date

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var adapter: PembayaranAdapter
    private val paymentViewModel: PaymentViewModel by viewModel()

    // Variabel untuk menyimpan informasi pembayaran
    private var selectedPaymentMethod: String? = null
    private var selectedItems: List<DataMenu>? = null
    private var quantitiesMap: Map<String, Int>? = null

    private lateinit var userId: String
    private val firestore = FirebaseFirestore.getInstance() // Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur window insets untuk edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mengambil sesi user (pastikan user login)
        paymentViewModel.getSession().observe(this) { session ->
            if (!session.isLogin) {
                finish() // Tutup activity jika user belum login
            } else {
                userId = session.userId
                binding.userId.text = userId
            }
        }

        // Ambil data dari intent
        selectedItems = intent.getParcelableArrayExtra("selectedItems")?.map { it as DataMenu }
        val totalCounts = intent.getIntExtra("totalCount", 0)
        val totalPrice = intent.getIntExtra("totalPrice", 0)
        val quantitiesArray = intent.getIntArrayExtra("itemQuantities") ?: intArrayOf()

        // Log untuk memastikan data intent diterima
        Log.d(
            "PaymentActivity",
            "Total Count: $totalCounts, Total Price: $totalPrice, Quantities: ${quantitiesArray.toList()}"
        )

        // Mengubah quantitiesArray menjadi Map dengan item ID sebagai key dan jumlah sebagai value
        quantitiesMap = selectedItems?.mapIndexed { index, item ->
            item.id to (quantitiesArray.getOrNull(index) ?: 0)
        }?.toMap() ?: emptyMap()

        // Update tampilan total harga
        binding.tvHarga.text = "Rp. $totalPrice"

        // Setup adapter untuk RecyclerView
        adapter = PembayaranAdapter(
            selectedItems ?: emptyList(),
            quantitiesMap ?: emptyMap()
        )
        binding.rvPembayaran.layoutManager = LinearLayoutManager(this)
        binding.rvPembayaran.adapter = adapter

        // Menambahkan listener untuk RadioGroup (pembayaran)
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            selectedPaymentMethod = when (checkedId) {
                R.id.radioCreditCard -> "Credit Card"
                R.id.radioDebitCard -> "Debit Card"
                R.id.radioEwallet -> "E-Wallet"
                else -> null
            }
            selectedPaymentMethod?.let {
                Log.d("PaymentActivity", "Metode pembayaran terpilih: $it")
            }
        }

        // Setup click listener untuk button Pesan
        binding.btnBayar.setOnClickListener {
            simpanPembayaran() // Simpan pembayaran saat tombol diklik
        }
    }

    // Fungsi untuk menyimpan pembayaran ke Firestore
    private fun simpanPembayaran() {
        if (selectedPaymentMethod != null && selectedItems != null && quantitiesMap != null) {
            val totalItems = quantitiesMap?.values?.sum() ?: 0
            val totalPrice =
                selectedItems?.sumBy { it.harga * (quantitiesMap?.get(it.id) ?: 0) } ?: 0
            val foreignKeyMenuIds = selectedItems?.map { it.id }

            val orderData = mapOf(
                "userId" to userId, // Foreign key ke user
                "items" to selectedItems?.map {
                    mapOf(
                        "id" to it.id,
                        "name" to it.nama,
                        "quantity" to quantitiesMap?.get(it.id),
                        "price" to it.harga
                    )
                },
                "totalItems" to totalItems,
                "totalPrice" to totalPrice,
                "paymentMethod" to selectedPaymentMethod,
                "timestamp" to Timestamp(Date()), // Waktu pesanan
                "status" to "pending", // Status awal
                "foreignKeyMenu" to foreignKeyMenuIds
            )

            firestore.collection("orders")
                .add(orderData)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "PaymentActivity",
                        "Pesanan berhasil disimpan dengan ID: ${documentReference.id}"
                    )
                    startActivity(Intent(this, OrderDetailActivity::class.java))
                    updateStockAfterOrder() // Update stok setelah pesanan berhasil
                    Toast.makeText(this, "Pesanan berhasil!", Toast.LENGTH_SHORT).show()
                    // Alihkan pengguna ke halaman sukses atau tampilan lainnya
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("PaymentActivity", "Error menyimpan pesanan: $e")
                    Toast.makeText(this, "Gagal menyimpan pesanan!", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d("PaymentActivity", "Data pembayaran tidak lengkap.")
            Toast.makeText(this, "Data pembayaran tidak lengkap!", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk update stok setelah order disimpan
    private fun updateStockAfterOrder() {
        selectedItems?.forEach { menuItem ->
            val quantityOrdered = quantitiesMap?.get(menuItem?.id) ?: 0

            if (quantityOrdered > 0) {
                val menuRef = firestore.collection("menu").document(menuItem.id)
                menuRef.get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val currentStock = document.getLong("stok")?.toInt() ?: 0
                            val updateStock = currentStock - quantityOrdered

                            if (updateStock >= 0) {
                                menuRef.update("stok", updateStock)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "PaymentActivity",
                                            "Stok berhasil diperbarui untuk ${menuItem.nama}"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("PaymentActivity", "Error updating stock: $e")
                                    }
                            } else {
                                Log.e(
                                    "PaymentActivity",
                                    "Stok tidak mencukupi untuk ${menuItem.nama}"
                                )
                                Toast.makeText(
                                    this,
                                    "Stok untuk item ${menuItem.nama} tidak cukup!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("PaymentActivity", "Error fetching menu data: $e")
                        Toast.makeText(this, "Error fetching menu data!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}


