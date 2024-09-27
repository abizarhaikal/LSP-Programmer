package com.example.lspprogrammer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lspprogrammer.databinding.ItemMenuUserBinding
import com.example.lspprogrammer.model.DataMenu

class MenuUserAdapter(
    private val dataPemesanan: List<DataMenu>,
    private val onItemCountChanged: (Int, Int) -> Unit // Callback untuk update jumlah total item dan total harga
) : RecyclerView.Adapter<MenuUserAdapter.MenuUserViewHolder>() {

    // Menyimpan jumlah item yang dipilih untuk setiap menu berdasarkan ID
    val itemCounts = mutableMapOf<String, Int>()

    inner class MenuUserViewHolder(val binding: ItemMenuUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dataMenu: DataMenu) {
            binding.tvTitleMenu.text = dataMenu.nama
            binding.tvPriceMenu.text = "Rp. ${dataMenu.harga}"
            binding.chipCategory.text = dataMenu.kategori

            Glide.with(itemView.context)
                .load(dataMenu.imageUri)
                .into(binding.ivMenu)

            // Ambil jumlah dari map atau 0 jika belum ada
            var count = itemCounts[dataMenu.id] ?: 0
            binding.tvItemCount.text = count.toString() // Update jumlah item di UI

            // Tambah item
            binding.btnAdd.setOnClickListener {
                count += 1
                itemCounts[dataMenu.id] = count
                binding.tvItemCount.text = count.toString() // Update jumlah item di UI
                updateItemCount()
            }

            // Kurangi item
            binding.btnSubtract.setOnClickListener {
                if (count > 0) {
                    count -= 1
                    itemCounts[dataMenu.id] = count
                    binding.tvItemCount.text = count.toString() // Update jumlah item di UI
                    updateItemCount()
                }
            }
        }

        private fun updateItemCount() {
            // Hitung total jumlah item
            val totalCount = itemCounts.values.sum()

            // Hitung total harga berdasarkan jumlah yang dipilih
            val totalPrice = itemCounts.map { (id, count) ->
                val menuItem = dataPemesanan.find { it.id == id }
                (menuItem?.harga ?: 0) * count
            }.sum()

            // Panggil callback untuk update total item dan total harga
            onItemCountChanged(totalCount, totalPrice)
        }
    }

    // Mengembalikan jumlah item yang dipilih
    fun getSelectedItems(): List<DataMenu> {
        return dataPemesanan.filter { itemCounts[it.id] ?: 0 > 0 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuUserViewHolder {
        // Inflate layout item menu
        val binding =
            ItemMenuUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuUserViewHolder, position: Int) {
        val dataMenu = dataPemesanan[position]
        holder.bind(dataMenu)
    }

    override fun getItemCount(): Int {
        return dataPemesanan.size
    }
}