package com.example.lspprogrammer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lspprogrammer.databinding.ItemPembayaranBinding
import com.example.lspprogrammer.model.DataMenu


class PembayaranAdapter(private val items: List<DataMenu>, private val quantities: Map<String, Int>) : RecyclerView.Adapter<PembayaranAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemPembayaranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    inner class MyViewHolder(val binding: ItemPembayaranBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataMenu, quantity: Int) {
            binding.tvItemName.text = item.nama
            binding.tvItemPrice.text = "Rp. ${item.harga}"
            binding.tvItemQuantity.text = "Jumlah: $quantity"
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        // Ambil quantity berdasarkan id item
        val quantity = quantities[item.id] ?: 0 // Mengambil quantity berdasarkan ID
        holder.bind(item, quantity)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

