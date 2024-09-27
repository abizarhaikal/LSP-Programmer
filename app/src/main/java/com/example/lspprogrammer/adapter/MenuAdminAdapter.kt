package com.example.lspprogrammer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lspprogrammer.databinding.ItemMenuBinding
import com.example.lspprogrammer.model.DataMenu

class MenuAdminAdapter(
    private val menuList: List<DataMenu>,
    private val itemClick: (DataMenu) -> Unit // Lambda for handling item clicks
) : RecyclerView.Adapter<MenuAdminAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuAdminAdapter.MenuViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    class MenuViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(menu: DataMenu, itemClick: (DataMenu) -> Unit) {
            binding.tvTitleMenu.text = menu.nama
            binding.tvPriceMenu.text = "Rp." + menu.harga.toString()
            binding.tvStokBarang.text = menu.stok.toString()
            binding.chipCategory.text = menu.kategori

            Glide.with(itemView.context)
                .load(menu.imageUri)
                .into(binding.ivMenu)

            // Set click listener for the item
            itemView.setOnClickListener {
                itemClick(menu) // Trigger the click event
            }
        }
    }

    override fun onBindViewHolder(holder: MenuAdminAdapter.MenuViewHolder, position: Int) {
        val menu = menuList[position]
        holder.bind(menu, itemClick) // Pass the item click listener to the ViewHolder
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
}