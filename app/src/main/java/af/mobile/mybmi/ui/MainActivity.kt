package af.mobile.mybmi.ui

import af.mobile.mybmi.theme.MintLight
import af.mobile.mybmi.theme.DarkGreenLovely
import af.mobile.mybmi.theme.getNavBarColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import af.mobile.mybmi.theme.myBMITheme
import af.mobile.mybmi.ui.history.HistoryDetailScreen
import af.mobile.mybmi.ui.history.HistoryScreen
import af.mobile.mybmi.ui.home.HomeScreen
import af.mobile.mybmi.ui.profile.ProfileScreen
import af.mobile.mybmi.ui.result.ResultScreen
import af.mobile.mybmi.ui.settings.SettingsScreen
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
import af.mobile.mybmi.database.BMIRepository
import af.mobile.mybmi.database.MyBMIDatabase
import af.mobile.mybmi.database.UserRepository
import af.mobile.mybmi.ui.splash.SplashScreen
import af.mobile.mybmi.viewmodel.ResultViewModel
import af.mobile.mybmi.viewmodel.ResultViewModelFactory
import af.mobile.mybmi.viewmodel.ThemeViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import af.mobile.mybmi.viewmodel.UserViewModelFactory
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
                // Initialize Database and Repositories
                val database = MyBMIDatabase.getDatabase(this@MainActivity)
                val userRepository = UserRepository(database.userDao())
                val bmiRepository = BMIRepository(database.bmiDao())

                MyBMIApp(
                    themeViewModel = themeViewModel,
                    userRepository = userRepository,
                    bmiRepository = bmiRepository
                )
            }
        }
    }
}

@Composable
fun MyBMIApp(
    themeViewModel: ThemeViewModel = viewModel(),
    userRepository: UserRepository? = null,
    bmiRepository: BMIRepository? = null
) {
    val navController = rememberNavController()
    val inputViewModel: InputViewModel = viewModel()

    // Create UserViewModel with factory
    val userViewModelFactory = if (userRepository != null) {
        UserViewModelFactory(userRepository)
    } else {
        null
    }
    val userViewModel = if (userViewModelFactory != null) {
        viewModel<UserViewModel>(factory = userViewModelFactory)
    } else {
        viewModel<UserViewModel>()
    }

    // Get current user and userId
    val currentUser by userViewModel.currentUser.collectAsState()
    val userId = currentUser?.id ?: 0  // This will be re-evaluated when currentUser changes

    // Create ResultViewModel with factory to provide BMIRepository
    val resultViewModelFactory = if (bmiRepository != null) {
        ResultViewModelFactory(bmiRepository, userId)
    } else {
        null
    }
    val resultViewModel = if (resultViewModelFactory != null) {
        viewModel<ResultViewModel>(factory = resultViewModelFactory)
    } else {
        viewModel<ResultViewModel>()
    }

    // Reload history whenever userId changes
    LaunchedEffect(userId) {
        if (userId > 0 && bmiRepository != null) {
            resultViewModel.loadHistory(userId)
        }
    }

    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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
                        navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                    },
                    userViewModel = userViewModel
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToResult = { summary ->
                        resultViewModel.setCurrentResult(summary)
                        navController.navigate(Screen.Result.route)
                    },
                    inputViewModel = inputViewModel,
                    userViewModel = userViewModel,
                    resultViewModel = resultViewModel
                )
            }
            composable(Screen.Result.route) {
                ResultScreen(onNavigateBack = { navController.popBackStack() }, resultViewModel = resultViewModel)
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateToDetail = { navController.navigate(Screen.HistoryDetail.route) },
                    resultViewModel = resultViewModel,
                    userViewModel = userViewModel
                )
            }
            composable(Screen.HistoryDetail.route) {
                HistoryDetailScreen(onNavigateBack = { navController.popBackStack() }, resultViewModel = resultViewModel)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    userViewModel = userViewModel
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(onNavigateBack = { navController.popBackStack() }, themeViewModel = themeViewModel)
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

    val selectedColor = Color.White

    val unselectedColor = if (isDarkMode) Color.Gray else MintLight

    val indicatorColor = if (isDarkMode) DarkGreenLovely else Color.White.copy(alpha = 0.2f)

    NavigationBar(
        containerColor = getNavBarColor(isDarkMode), // Akan hijau di Light Mode
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.History,
            BottomNavItem.Profile
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Image(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(
                            if (isSelected) selectedColor else unselectedColor
                        )
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) selectedColor else unselectedColor
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = indicatorColor
                )
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Result : Screen("result")
    object History : Screen("history")
    object HistoryDetail : Screen("history_detail")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}

sealed class BottomNavItem(val route: String, val iconRes: Int, val label: String) {
    object Home : BottomNavItem(Screen.Home.route, R.drawable.icon1, "Beranda")
    object History : BottomNavItem(Screen.History.route, R.drawable.icon3, "Riwayat")
    object Profile : BottomNavItem(Screen.Profile.route, R.drawable.icon2, "Profil")
}