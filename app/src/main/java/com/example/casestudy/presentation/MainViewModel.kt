package com.example.casestudy.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.casestudy.roomqr.AppDatabase
import com.example.casestudy.roomqr.QRData
import com.example.casestudy.domain.repo.MainRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: MainRepo,
    application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java, "database-name"
    ).build()

    internal val qrDataDao = db.qrDataDao()

    fun startScanning() {
        viewModelScope.launch {
            //Reset state value saat menggunakan QR Scan lagi
            _state.value = MainScreenState()
            repo.startScanning().collect {
                if (!it.isNullOrBlank()) {
                    _state.value = state.value.copy(
                        details = it
                    )

                    val qrDataList = it.split("\n")
                    if (qrDataList.size >= 4) {
                        val bank = qrDataList[0].substring(6)
                        val id = qrDataList[1].substring(4)
                        val name = qrDataList[2].substring(6)
                        val transaction = qrDataList[3].substring(13)

                        //Mengupdate nilai transaksi dalam state
                        _state.value = state.value.copy(transaction = transaction.toInt())

                        val qrData = QRData(
                            bank = bank,
                            transactionId = id,
                            name = name,
                            transaction = transaction
                        )
                        Log.d("QR Scan", "Data yang dimasukkan ke database: $qrData")
                        try {
                            withContext(Dispatchers.IO) {
                                qrDataDao.insert(qrData)
                            }
                        } catch (e: Exception) {
                            Log.e("QR Scan", "Error data tidak masuk ke database", e)
                        }

                    } else {
                        //else
                    }
                }
            }
        }
    }

    fun resetState() {
        _state.value = MainScreenState()
    }

}