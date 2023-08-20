package com.example.casestudy.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.casestudy.data.dc.DonutChartData
import com.example.casestudy.domain.repo.ChartRepository

@Composable
fun ChartScreen(navController: NavController, chartRepository: ChartRepository) {
    val data = chartRepository.getChartData()
    val donutChartData = data.first { it.type == "donutChart" } as DonutChartData
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Portofolio", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            donutChartData.data.forEach { chartData ->
                Button(onClick = {
                    navController.navigate("chartDetail/${chartData.label}")
                }) {
                    Text(text = chartData.label)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartDetailScreen(navController: NavController, label: String, chartRepository: ChartRepository) {
    val data = chartRepository.getChartData()
    val donutChartDetails = (data.first { it.type == "donutChart" } as DonutChartData).data.first { it.label == label }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "${donutChartDetails.label} Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        content = { padding ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.padding(padding), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Percentage: ${donutChartDetails.percentage}", style = MaterialTheme.typography.titleLarge)
                    LazyColumn {
                        items(donutChartDetails.data) { detailData ->
                            Text(text = "${detailData.trx_date}: ${detailData.nominal}", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }
    )
}