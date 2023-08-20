package com.example.casestudy.roomqr

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [QRData::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun qrDataDao(): QRDataDao
}
