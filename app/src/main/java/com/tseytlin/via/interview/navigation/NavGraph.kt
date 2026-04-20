package com.tseytlin.via.interview.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tseytlin.via.interview.detail.RequestDetailScreen
import com.tseytlin.via.interview.home.HomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onCreateRequest = { navController.navigate("detail") })
        }
        composable("detail") {
            RequestDetailScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
