package com.example.casestudy.data.dc

data class Promo(
    val id: String,
    val nama: String,
    val desc: String,
    val lokasi: String,
    val img: PromoImage
)

data class PromoImage(
    val url: String
)
