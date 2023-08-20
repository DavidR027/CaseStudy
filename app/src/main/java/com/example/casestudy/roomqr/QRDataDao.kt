package com.example.casestudy.roomqr

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QRDataDao {
    @Query("SELECT * FROM qrdata")
    fun getAll(): List<QRData>

    @Insert
    fun insert(qrData: QRData)

    @Delete
    fun delete(qrData: QRData)

    @Query("DELETE FROM qrdata")
    fun deleteAll()
}
