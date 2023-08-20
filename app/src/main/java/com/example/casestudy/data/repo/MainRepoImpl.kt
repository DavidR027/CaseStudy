package com.example.casestudy.data.repo

import com.example.casestudy.domain.repo.MainRepo
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainRepoImpl @Inject constructor(
    private val scanner: GmsBarcodeScanner,
) : MainRepo {


    override fun startScanning(): Flow<String?> {
        return callbackFlow {
            scanner.startScan()
                .addOnSuccessListener {
                    launch {
                        send(getDetails(it))
                    }
                }.addOnFailureListener {
                    it.printStackTrace()
                }
            awaitClose {  }
        }

    }



    private fun getDetails(barcode: Barcode): String {
        return when (barcode.valueType) {
            Barcode.TYPE_TEXT -> {
                val qrData = barcode.rawValue
                if (qrData != null) {
                    val qrDataList = qrData.split(".")
                    val bank = qrDataList[0]
                    val id = qrDataList[1].substring(2)
                    val name = qrDataList[2]
                    val transaction = qrDataList[3]

                    "Bank: $bank\nID: $id\nName: $name\nTransaction: $transaction"
                } else {
                    "QR code data is null"
                }
            }
            Barcode.TYPE_UNKNOWN -> {
                "unknown : ${barcode.rawValue}"
            }
            else -> {
                "Couldn't determine"
            }
        }

    }
}