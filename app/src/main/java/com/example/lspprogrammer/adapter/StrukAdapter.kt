package com.example.lspprogrammer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lspprogrammer.databinding.ItemRiwayatOrderBinding
import com.example.lspprogrammer.model.DataStruk
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StrukAdapter(
    private val strukList: List<DataStruk>,
    private val onItemClick: (DataStruk) -> Unit // Tambahkan parameter untuk callback
) : RecyclerView.Adapter<StrukAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StrukAdapter.MyViewHolder {
        val binding =
            ItemRiwayatOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    inner class MyViewHolder(private val binding: ItemRiwayatOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(struk: DataStruk) {
            binding.chipStatus.text = struk.status
            binding.tvOrderId.text = struk.orderId

            // Menyiapkan item details
            val itemNames = struk.items.joinToString(separator = "\n") { it.name }
            val itemQuantities = struk.items.joinToString(separator = "\n") { "x" + it.quantity }
            val itemPrices = struk.items.joinToString(separator = "\n") { "Rp." + it.price }

            // Format Timestamp
            val timestamp: Timestamp = struk.timeStamp
            val date: Date = timestamp.toDate()
            val formatter = SimpleDateFormat("dd MMMM yyyy 'at' hh:mm:ss a", Locale("id", "ID"))
            val formattedDate = formatter.format(date)

            binding.tvMetode.text = struk.paymentMethod
            binding.tvTimeStamp.text = formattedDate
            binding.tvMenu.text = itemNames
            binding.jumlahMenu.text = itemQuantities
            binding.hargaMenu.text = itemPrices

            // Mengatur click listener
            binding.root.setOnClickListener {
                onItemClick(struk) // Panggil callback saat item di klik
            }
        }
    }

    override fun onBindViewHolder(holder: StrukAdapter.MyViewHolder, position: Int) {
        val struk = strukList[position]
        holder.bind(struk)
    }

    override fun getItemCount(): Int {
        return strukList.size
    }
}
