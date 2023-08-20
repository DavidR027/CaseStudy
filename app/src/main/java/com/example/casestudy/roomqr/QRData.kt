package com.example.casestudy.roomqr

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QRData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bank: String,
    val transactionId: String,
    val name: String,
    val transaction: String
)
