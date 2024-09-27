package com.example.lspprogrammer.model

import com.google.firebase.Timestamp

data class DataStruk(
    val orderId: String,
    val timeStamp: Timestamp,
    val status: String,
    val items: List<OrderItems>,
    val totalPrice: Int,
    val paymentMethod: String
)

data class OrderItems(
    val id: String,
    val name: String,
    val price: String,
    val quantity: String
)