package com.example.myactivity.data.model
//상품 정보
data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    val quantity: Int,
    val companyUserId: Long,
)