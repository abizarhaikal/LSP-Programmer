package com.example.lspprogrammer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lspprogrammer.databinding.ItemRiwayatOrderBinding
import com.example.lspprogrammer.model.Laporan
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportAdapter(private val reportList: List<Laporan>) :
    RecyclerView.Adapter<ReportAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportAdapter.MyViewHolder {
        val binding =
            ItemRiwayatOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportAdapter.MyViewHolder, position: Int) {
        val report = reportList[position]
        holder.bind(report)
    }

    class MyViewHolder(private val binding: ItemRiwayatOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(report: Laporan) {
            binding.chipStatus.text = report.status
            binding.tvOrderId.text = report.orderId

            val itemNames = report.items.joinToString("\n") { it.name }
            val itemQuantities = report.items.joinToString(
                separator =
                "\n"
            ) { " x " + it.quantity }
            val itemPrices = report.items.joinToString("\n") { " Rp ." + it.price }

            val timeStamp: Timestamp = report.timestamp
            val date: Date = timeStamp.toDate()
            val formatter = SimpleDateFormat("dd MMMM yyyy 'at' hh:mm:ss a", Locale("id", "ID"))
            val formattedDate = formatter.format(date)

            binding.tvMetode.text = report.paymentMethod
            binding.tvTimeStamp.text = formattedDate
            binding.tvMenu.text = itemNames
            binding.jumlahMenu.text = itemQuantities
            binding.hargaMenu.text = itemPrices
        }

    }

    override fun getItemCount(): Int {
        return reportList.size
    }

}