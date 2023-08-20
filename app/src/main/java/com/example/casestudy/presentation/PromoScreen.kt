package com.example.casestudy.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.casestudy.data.repo.Promo
import com.example.casestudy.data.repo.fetchPromos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PromoScreen(navController: NavController) {
    val promos = remember { mutableStateOf<List<Promo>>(emptyList()) }
    LaunchedEffect(Unit) {
        promos.value = withContext(Dispatchers.IO) { fetchPromos() }
    }
    Column {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(text = "Promo BNI", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
        }
        LazyColumn {
            items(promos.value.chunked(2)) { row ->
                Row(Modifier.fillMaxWidth()) {
                    row.forEach { promo ->
                        PromoCard(promo, Modifier.weight(1f)) {
                            navController.navigate("promoDetail/${promo.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PromoCard(promo: Promo, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = rememberImagePainter(promo.img.url),
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(text = promo.nama, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromoDetailScreen(navController: NavController, promoId: String) {
    val promo = remember { mutableStateOf<Promo?>(null) }
    LaunchedEffect(promoId) {
        promo.value = withContext(Dispatchers.IO) { fetchPromos().first { it.id == promoId } }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Promo Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (promo.value != null) {
            Column(modifier = Modifier.padding(padding)) {
                Image(
                    painter = rememberImagePainter(promo.value!!.img.url),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = promo.value!!.nama, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = promo.value!!.desc, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Lokasi: ${promo.value!!.lokasi}", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}