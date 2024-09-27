package com.example.lspprogrammer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lspprogrammer.databinding.ItemPembayaranBinding
import com.example.lspprogrammer.model.OrderItem

class OrderAdapter(private var items: List<OrderItem>) :
    RecyclerView.Adapter<OrderAdapter.MyViewHolder>() {

    fun updateItems(newItems: List<OrderItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemPembayaranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class MyViewHolder(private val binding: ItemPembayaranBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderItem) {
            binding.tvItemName.text = item.name
            binding.tvItemPrice.text = "Rp. ${item.price}"
            binding.tvItemQuantity.text = "x${item.quantity}"
        }
    }
}