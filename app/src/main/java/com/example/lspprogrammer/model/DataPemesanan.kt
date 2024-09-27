package com.example.lspprogrammer.model

data class DataPemesanan (
    val total : Int = 0,
    val harga : Int = 0,
    val pembayaran : String = "",
    val waktu : String = "",
    val status : String = "",
    val menu : List<DataMenu> = listOf(),
    val id : String = "",
    val userId : String = "",
)