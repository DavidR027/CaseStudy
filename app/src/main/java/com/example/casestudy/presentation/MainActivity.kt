package com.example.casestudy.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.casestudy.R
import com.example.casestudy.ui.theme.CaseStudyTheme
import dagger.hilt.android.AndroidEntryPoint

@Composable
fun FirstScreen(navController: NavController) {
    PromoScreen(navController)
}

@Composable
fun SecondScreen() {
    CaseStudyTheme {
        Surface(modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            MainScreen()
        }
    }
}

@Composable
fun ThirdScreen() {
    Text(text = "This is the chart screen")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            Scaffold(
                bottomBar = {
                    TabRow(selectedTabIndex = when {
                        currentRoute == "first" -> 0
                        currentRoute == "second" -> 1
                        currentRoute?.startsWith("promoDetail") == true -> 0
                        else -> 2
                    }) {
                        Tab(
                            icon = { Icon(painterResource(id = R.drawable.ic_promos), contentDescription = null) },
                            text = { Text("Promos") },
                            selected = currentRoute == "first",
                            onClick = {
                                navController.navigate("first") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        Tab(
                            icon = { Icon(painterResource(id = R.drawable.ic_scan), contentDescription = null) },
                            text = { Text("Scan") },
                            selected = currentRoute == "second",
                            onClick = {
                                navController.navigate("second") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        Tab(
                            icon = { Icon(painterResource(id = R.drawable.ic_chart), contentDescription = null) },
                            text = { Text("Chart") },
                            selected = currentRoute == "third",
                            onClick = {
                                navController.navigate("third") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            ) {
                NavHost(navController, startDestination = "second", Modifier.padding(bottom=60.dp)) {
                    composable("first") { FirstScreen(navController) }
                    composable("second") { SecondScreen() }
                    composable("third") { ThirdScreen() }
                    composable("promoDetail/{promoId}") { backStackEntry ->
                        PromoDetailScreen(navController, backStackEntry.arguments?.getString("promoId")!!)
                    }
                }
            }
        }
    }
}
