package com.example.lspprogrammer.ui.administrator.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lspprogrammer.adapter.MenuAdminAdapter
import com.example.lspprogrammer.databinding.FragmentMenuBinding
import com.example.lspprogrammer.model.DataMenu
import com.google.firebase.firestore.FirebaseFirestore

class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var adapter: MenuAdminAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val menuList = mutableListOf<DataMenu>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView with LayoutManager
        binding.rvMenuAdmin.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter and set it to RecyclerView
        adapter = MenuAdminAdapter(menuList) { menuItem -> onMenuItemClick(menuItem) }
        binding.rvMenuAdmin.adapter = adapter

        // Fetch data from Firestore
        fetchMenuData()

        // Setup FAB to navigate to AddMenuActivity
        binding.fabAdd.setOnClickListener { startView() }
    }

    private fun startView() {
        startActivity(Intent(requireContext(), AddMenuActivity::class.java).apply {
            putExtra(AddMenuActivity.ADD_MENU, 100) // Signal for adding a menu
        })
    }

    private fun onMenuItemClick(menuItem: DataMenu) {
        // Start AddMenuActivity in update mode and pass the selected menu item's ID
        startActivity(Intent(requireContext(), AddMenuActivity::class.java).apply {
            putExtra(AddMenuActivity.ADD_MENU, 200) // Signal for updating a menu
            putExtra("menuId", menuItem.id) // Pass the menu ID for updating
        })
    }

    private fun fetchMenuData() {
        firestore.collection("menu")
            .get()
            .addOnSuccessListener { documents ->
                menuList.clear() // Clear the list before adding new data
                if (documents.isEmpty) {
                    binding.rvMenuAdmin.visibility = View.GONE
                    // Optionally, show a message for empty data
                } else {
                    binding.rvMenuAdmin.visibility = View.VISIBLE
                    for (document in documents) {
                        var menuItem = document.toObject(DataMenu::class.java)
                        menuItem.id = document.id // Set the document ID to the menu item
                        menuList.add(menuItem)
                    }
                    adapter.notifyDataSetChanged() // Notify adapter that data has changed
                }
            }
            .addOnFailureListener { e ->
                Log.e("MenuFragment", "Error fetching menu data: $e")
            }
    }

    override fun onResume() {
        super.onResume()
        fetchMenuData()
        adapter.notifyDataSetChanged()
    }
}
