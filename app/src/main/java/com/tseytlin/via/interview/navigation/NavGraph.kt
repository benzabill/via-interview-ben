package com.tseytlin.via.interview.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tseytlin.via.interview.detail.RequestDetailScreen
import com.tseytlin.via.interview.domain.model.Request
import com.tseytlin.via.interview.home.HomeScreen
import com.tseytlin.via.interview.home.viewmodel.RequestSharedViewModel
import org.koin.androidx.compose.koinViewModel

private val mockRequest = Request(
    id = "1",
    title = "Heading 1",
    description = "Lorem ipsum dolor sit amet consectetur. Arcu tincidunt vitae cras amet. " +
        "Blandit id sed et est gravida. Eu sapien amet et volutpat ultrices sed. " +
        "Euismod semper mi non vitae egestas sollicitudin aliquam.",
)

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
                request = mockRequest,
                onNavigateBack = { outcome ->
                    sharedViewModel.emitOutcome(outcome)
                    navController.popBackStack()
                },
            )
        }
    }
}
