package af.mobile.mybmi.ui

import af.mobile.mybmi.theme.Gray400
import af.mobile.mybmi.theme.GreenPrimary
import af.mobile.mybmi.theme.MintLight
import af.mobile.mybmi.theme.getNavBarColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import af.mobile.mybmi.theme.myBMITheme
import af.mobile.mybmi.ui.history.HistoryDetailScreen
import af.mobile.mybmi.ui.history.HistoryScreen
import af.mobile.mybmi.ui.home.HomeScreen
import af.mobile.mybmi.ui.profile.ProfileScreen
import af.mobile.mybmi.ui.result.ResultScreen
import af.mobile.mybmi.ui.settings.SettingsScreen
import af.mobile.mybmi.ui.splash.SplashScreen
import af.mobile.mybmi.viewmodel.InputViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import af.mobile.mybmi.R
import af.mobile.mybmi.viewmodel.ResultViewModel
import af.mobile.mybmi.viewmodel.ThemeViewModel
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            myBMITheme(isDarkMode = isDarkMode) {
                MyBMIApp(themeViewModel = themeViewModel)
            }
        }
    }
}

@Composable
fun MyBMIApp(themeViewModel: ThemeViewModel = viewModel()) {
    val navController = rememberNavController()
    val inputViewModel: InputViewModel = viewModel()
    val resultViewModel: ResultViewModel = viewModel()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Routes yang menampilkan bottom bar
    val routesWithBottomBar = listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in routesWithBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    isDarkMode = isDarkMode
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToResult = {
                        // Get current result from inputViewModel and pass to resultViewModel
                        inputViewModel.calculateBMI { summary ->
                            resultViewModel.setCurrentResult(summary)
                            navController.navigate(Screen.Result.route)
                        }
                    },
                    inputViewModel = inputViewModel
                )
            }

            composable(Screen.Result.route) {
                ResultScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    resultViewModel = resultViewModel
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateToDetail = {
                        navController.navigate(Screen.HistoryDetail.route)
                    },
                    resultViewModel = resultViewModel
                )
            }

            composable(Screen.HistoryDetail.route) {
                HistoryDetailScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    resultViewModel = resultViewModel
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?,
    isDarkMode: Boolean = false
) {
    NavigationBar(
        containerColor = getNavBarColor(isDarkMode),
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.History,
            BottomNavItem.Profile
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(
                            if (currentRoute == item.route) GreenPrimary else Gray400
                        )
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (currentRoute == item.route) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        }
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GreenPrimary,
                    selectedTextColor = GreenPrimary,
                    unselectedIconColor = Gray400,
                    unselectedTextColor = Gray400,
                    indicatorColor = MintLight
                )
            )
        }
    }
}

// Navigation routes
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Result : Screen("result")
    object History : Screen("history")
    object HistoryDetail : Screen("history_detail")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}

// Bottom nav items
sealed class BottomNavItem(
    val route: String,
    val iconRes: Int,
    val label: String
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        iconRes = R.drawable.icon1,
        label = "Beranda"
    )

    object History : BottomNavItem(
        route = Screen.History.route,
        iconRes = R.drawable.icon3,
        label = "Riwayat"
    )

    object Profile : BottomNavItem(
        route = Screen.Profile.route,
        iconRes = R.drawable.icon2,
        label = "Profil"
    )
}