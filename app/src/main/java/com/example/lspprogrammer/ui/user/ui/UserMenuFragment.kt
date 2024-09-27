package com.example.lspprogrammer.ui.user.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lspprogrammer.adapter.MenuUserAdapter
import com.example.lspprogrammer.databinding.FragmentUserMenuBinding
import com.example.lspprogrammer.model.DataMenu
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.firebase.firestore.FirebaseFirestore

class UserMenuFragment : Fragment() {
    private lateinit var binding: FragmentUserMenuBinding
    private lateinit var adapter: MenuUserAdapter
    private val menuList = mutableListOf<DataMenu>()
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var badgeDrawable: BadgeDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(ExperimentalBadgeUtils::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        binding.rvMenuAdmin.layoutManager = LinearLayoutManager(requireContext())

        // Set up BadgeDrawable for FAB
        badgeDrawable = BadgeDrawable.create(requireContext()).apply {
            number = 0 // Set initial count to 0
            isVisible = false // Initially hide the badge
        }

        // Attach BadgeDrawable after FAB is laid out
        binding.fabAdd.post {
            BadgeUtils.attachBadgeDrawable(badgeDrawable, binding.fabAdd)
        }

        // Initialize adapter with menu list and update badge callback
        adapter = MenuUserAdapter(menuList) { totalCount, totalPrice ->
            updateBadgeCount(totalCount)
        }
        binding.rvMenuAdmin.adapter = adapter

        // Setup FAB click listener to navigate to PaymentActivity
        binding.fabAdd.setOnClickListener {
            navigateToPaymentActivity()
        }

        // Fetch menu data from Firestore
        fetchMenuData()
    }

    private fun updateBadgeCount(count: Int) {
        badgeDrawable.number = count // Update badge number
        badgeDrawable.isVisible = count > 0 // Show or hide badge based on count
    }

    private fun fetchMenuData() {
        firestore.collection("menu")
            .get()
            .addOnSuccessListener { documents ->
                menuList.clear() // Clear the list before adding new data
                if (documents.isEmpty) {
                    binding.rvMenuAdmin.visibility = View.GONE
                } else {
                    binding.rvMenuAdmin.visibility = View.VISIBLE
                    for (document in documents) {
                        val menuItem = document.toObject(DataMenu::class.java)
                        menuItem.id = document.id // Set the document ID to the menu item
                        menuList.add(menuItem)
                    }
                    adapter.notifyDataSetChanged() // Notify adapter that data has changed
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserMenuFragment", "Error fetching menu data: $e")
            }
    }

    private fun navigateToPaymentActivity() {
        val selectedItems = adapter.getSelectedItems() // Ambil item yang dipilih
        val totalCount = selectedItems.sumOf { adapter.itemCounts[it.id] ?: 0 }
        val totalPrice = selectedItems.sumOf { (it.harga * (adapter.itemCounts[it.id] ?: 0)) }

        // Kirim item quantities dalam bentuk Map<String, Int>
        val itemQuantities =
            adapter.itemCounts.map { it.value }.toIntArray() // Mengambil semua jumlah item

        // Buat intent untuk PaymentActivity
        checkStockAvailibilty(selectedItems) { stockAvailable, insufficientStockItems ->
            if (stockAvailable) {
                val intent = Intent(requireContext(), PaymentActivity::class.java).apply {
                    putExtra(
                        "selectedItems",
                        selectedItems.toTypedArray()
                    ) // Kirim data menu yang dipilih
                    putExtra("totalCount", totalCount) // Kirim total jumlah item
                    putExtra("totalPrice", totalPrice) // Kirim total harga
                    putExtra("itemQuantities", itemQuantities) // Kirim jumlah item sebagai IntArray
                }
                startActivity(intent) // Mulai Activity Pembayaran
            } else {
                insufficientStockItems.forEach { (menuItem, availableStock) ->
                    val requiredQuantity = adapter.itemCounts[menuItem.id] ?: 0
                    Toast.makeText(
                        requireContext(),
                        "Stok untuk ${menuItem.nama} tidak mencukupi. Tersisa $availableStock, Anda memesan $requiredQuantity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun checkStockAvailibilty(
        selectedItems: List<DataMenu>,
        callback: (Boolean, List<Pair<DataMenu, Int>>) -> Unit
    ) {
        var allStockAvailable = true
        val insufficientStockItems = mutableListOf<Pair<DataMenu,Int>>()
        selectedItems.forEach { menuItem ->
            val requiredQuantity = adapter.itemCounts[menuItem.id] ?: 0

            firestore.collection("menu")
                .document(menuItem.id)
                .get()
                .addOnSuccessListener { document ->
                    val availableStock = document.getLong("stok")?.toInt() ?: 0
                    if (requiredQuantity > availableStock) {
                        allStockAvailable = false
                        insufficientStockItems.add(menuItem to availableStock)
                    }

                    if (selectedItems.last() == menuItem) {
                        callback(allStockAvailable, insufficientStockItems)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("UserMenuFragment", "Error checking stock availability: $e")
                    allStockAvailable = false
                    if (selectedItems.last() == menuItem) {
                        callback(allStockAvailable, insufficientStockItems)
                    }
                }
        }
    }


}