package com.tseytlin.via.interview.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tseytlin.via.interview.detail.RequestDetailScreen
import com.tseytlin.via.interview.home.HomeScreen
import com.tseytlin.via.interview.home.viewmodel.RequestSharedViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val sharedViewModel: RequestSharedViewModel = koinViewModel()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onCreateRequest = { navController.navigate("detail") },
                sharedViewModel = sharedViewModel,
            )
        }
        composable("detail") {
            RequestDetailScreen(
                onNavigateBack = { outcome ->
                    sharedViewModel.emitOutcome(outcome)
                    navController.popBackStack()
                },
            )
        }
    }
}
