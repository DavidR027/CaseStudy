package com.example.casestudy.presentation

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.casestudy.roomqr.QRData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    //Mengambil semua data dari database
    var allData by remember { mutableStateOf<List<QRData>>(emptyList()) }

    //Nilai saldo awal
    var saldo by rememberSaveable { mutableStateOf(250000) }

    //Muncul alert ketika dibutuhkan
    var showDialog by remember { mutableStateOf(false) }
    var showFailedDialog by remember { mutableStateOf(false) }

    //Untuk buka atau tutup riwayat
    var showData by remember { mutableStateOf(false) }

    //SharedPreference untuk menyimpan saldo
    val sharedPreferences = LocalContext.current.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    //Menyimpan nilai saldo dalam SharedPrefernce setelah perubahan
    LaunchedEffect(saldo) {
        with(sharedPreferences.edit()) {
            putInt("saldo", saldo)
            apply()
        }
    }

    //Memunculkan saldo dari SharedPreference ketika membuka halaman ini
    LaunchedEffect(Unit) {
        saldo = sharedPreferences.getInt("saldo", 250000)
    }

    //Mengubah nilai saldo dan memunculkan alert ketika terjadi transaksi
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(state.transaction) {
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED && state.transaction != null) {
            if (saldo - state.transaction!! >= 0) {
                saldo -= state.transaction!!
                showDialog = true
            } else {
                showFailedDialog = true
            }
        }
    }

    //Alert transaksi berhasil
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Transaksi Berhasil") },
            text = { Text(text = "Saldo anda berkurang Rp ${state.transaction}") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = "OK")
                }
            }
        )
    }

    //Alert transaksi gagal
    if (showFailedDialog) {
        AlertDialog(
            onDismissRequest = { showFailedDialog = false },
            title = { Text(text = "Transaksi Gagal") },
            text = { Text(text = "Maaf transaksi tidak dapat dilakukan") },
            confirmButton = {
                Button(onClick = { showFailedDialog = false }) {
                    Text(text = "OK")
                }
            }
        )
    }

    //Tampilan Saldo
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Divider()
        Text(text = "Saldo: $saldo")
        Divider()

        //Tampilan Detail
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "[Detail Transaksi]",
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth()
            )
            if (state.details.isNotEmpty()) {
                val qrDataList = state.details.split("\n")
                if (qrDataList.size >= 4) {
                    val name = qrDataList[2].substring(6)
                    val transaction = qrDataList[3].substring(13)
                    val transactionId = qrDataList[1].substring(4)

                    Column {
                        Text(text = "Nama Merchant: $name", textAlign = TextAlign.Justify, modifier = Modifier.fillMaxWidth())
                        Text(text = "Nominal Transaksi: $transaction", textAlign = TextAlign.Justify, modifier = Modifier.fillMaxWidth())
                        Text(text = "ID Transaksi: $transactionId", textAlign = TextAlign.Justify, modifier = Modifier.fillMaxWidth())
                    }
                }
            } else {
                Text(text = "Scan untuk Detail", textAlign = TextAlign.Justify, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier=Modifier.height(16.dp))

            Button(onClick = { viewModel.startScanning() }) {
                Text(text = "Scan QR")
            }
        }
        Divider()
        Spacer(modifier=Modifier.height(16.dp))

        //Tombol buka/tutup riwayat
        Row(verticalAlignment=Alignment.CenterVertically) {
            Button(onClick = {
                //Menggunakan coroutine untuk melakukan operasi database pada background thread
                viewModel.viewModelScope.launch {
                    allData = withContext(Dispatchers.IO) {
                        viewModel.qrDataDao.getAll()
                    }
                    //Melihat value dari allData
                    Log.d("Data Transaksi", "allData: $allData")
                }
                showData=true
            }) {
                Text(text="Buka Riwayat")
            }
            Spacer(modifier=Modifier.width(8.dp))
            Button(onClick={showData=false}) {
                Text(text="Tutup Riwayat")
            }

        }

        //Muncul kolom ketika pilih buka riwayat
        if(showData){
            Column(modifier=Modifier.fillMaxWidth()){
                Text(text="Riwayat Transaksi", style=MaterialTheme.typography.headlineLarge, modifier=Modifier.padding(16.dp))
                Divider()
                LazyColumn{
                    items(allData){data->
                        Row(modifier=Modifier.padding(16.dp)){
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text="Nama Merchant: ${data.name}", style=MaterialTheme.typography.bodyLarge)
                                Spacer(modifier=Modifier.height(4.dp))
                                Text(text="Nominal Transaksi: ${data.transaction}", style=MaterialTheme.typography.bodyLarge)
                            }
                            Button(onClick={
                                viewModel.viewModelScope.launch{
                                    withContext(Dispatchers.IO){
                                        viewModel.qrDataDao.delete(data)
                                    }
                                    allData=withContext(Dispatchers.IO){
                                        viewModel.qrDataDao.getAll()
                                    }
                                }
                            }){
                                Text(text="Hapus")
                            }
                        }
                        Divider()
                    }
                }
            }
        }
    }
}