package dev.havlicektomas.photosapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.havlicektomas.photosapp.feature.detail.presentation.DetailRoot
import dev.havlicektomas.photosapp.feature.home.presentation.HomeRoot

@Composable
fun RootNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeRoot(
                onNavigateToDetail = { photo ->
                    navController.navigate(photo.toDetailRoute())
                },
            )
        }
        composable<DetailRoute>(typeMap = DetailRouteTypeMap) {
            DetailRoot(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
