package com.example.lspprogrammer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataMenu (
    val nama : String = "",
    val harga : Int = 0,
    val kategori: String = "",
    val imageUri : String = "",
    val stok : Int = 0,
    var id : String =""
) : Parcelable