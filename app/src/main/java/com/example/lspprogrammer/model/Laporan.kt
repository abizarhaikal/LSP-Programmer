package com.example.lspprogrammer.model

import com.google.firebase.Timestamp

data class Laporan (
    val orderId : String,
    val timestamp: Timestamp,
    val status : String,
    val items : List<LaporanItems>,
    val totalPrice : Int,
    val paymentMethod: String
)

data class LaporanItems(
    val id : String,
    val name : String,
    val price : String,
    val quantity : String

)