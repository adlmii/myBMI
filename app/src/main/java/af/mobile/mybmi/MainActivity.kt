package af.mobile.mybmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// --- IMPORT SCREENS ---
import af.mobile.mybmi.screens.history.HistoryDetailScreen
import af.mobile.mybmi.screens.history.HistoryScreen
import af.mobile.mybmi.screens.home.HomeScreen
import af.mobile.mybmi.screens.profile.ProfileScreen
import af.mobile.mybmi.screens.result.ResultScreen
import af.mobile.mybmi.screens.settings.SettingsScreen
import af.mobile.mybmi.screens.splash.SplashScreen
import af.mobile.mybmi.screens.profile.EditProfileScreen

// --- IMPORTS DATA & LOGIC ---
import af.mobile.mybmi.database.BMIRepository
import af.mobile.mybmi.database.MyBMIDatabase
import af.mobile.mybmi.database.UserRepository
import af.mobile.mybmi.database.BadgeDao // Import BadgeDao
import af.mobile.mybmi.database.BMIDao   // Import BMIDao
import af.mobile.mybmi.screens.settings.PrivacyPolicyScreen
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()

            // Setup Theme (Auto/Manual)
            val systemInDarkTheme = isSystemInDarkTheme()
            LaunchedEffect(Unit) {
                themeViewModel.initializeTheme(systemInDarkTheme)
            }
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            myBMITheme(isDarkMode = isDarkMode) {
                // Inisialisasi Database & DAO
                val database = MyBMIDatabase.getDatabase(this@MainActivity)
                val userRepository = UserRepository(database.userDao())
                val bmiRepository = BMIRepository(database.bmiDao())

                // Ambil DAO tambahan untuk fitur Badge
                val badgeDao = database.badgeDao()
                val bmiDao = database.bmiDao()

                // Jalankan App
                MyBMIApp(
                    themeViewModel = themeViewModel,
                    userRepository = userRepository,
                    bmiRepository = bmiRepository,
                    badgeDao = badgeDao, // Pass ke Composable Utama
                    bmiDao = bmiDao      // Pass ke Composable Utama
                )
            }
        }
    }
}

@Composable
fun MyBMIApp(
    themeViewModel: ThemeViewModel = viewModel(),
    userRepository: UserRepository? = null,
    bmiRepository: BMIRepository? = null,
    badgeDao: BadgeDao? = null, // Parameter untuk Badge
    bmiDao: BMIDao? = null      // Parameter untuk Badge Logic
) {
    val navController = rememberNavController()

    // 1. SHARED VIEWMODELS (Input & Reminder)
    // Dibuat di sini agar statenya terjaga antar screen
    val inputViewModel: InputViewModel = viewModel()
    val reminderViewModel: ReminderViewModel = viewModel()

    // 2. USER VIEWMODEL SETUP
    // Menggunakan Factory untuk inject Repository & BadgeDao
    val userViewModelFactory = if (userRepository != null) {
        UserViewModelFactory(userRepository, badgeDao)
    } else null

    val userViewModel = if (userViewModelFactory != null) {
        viewModel<UserViewModel>(factory = userViewModelFactory)
    } else {
        viewModel<UserViewModel>()
    }

    // Ambil User ID untuk keperluan ResultViewModel
    val currentUser by userViewModel.currentUser.collectAsState()
    val userId = currentUser?.id ?: 0

    // 3. RESULT VIEWMODEL SETUP
    // Menggunakan Factory untuk inject Repo, BadgeDao, BMIDao, dan UserId
    val resultViewModelFactory = if (bmiRepository != null && badgeDao != null && bmiDao != null) {
        ResultViewModelFactory(bmiRepository, badgeDao, bmiDao, userId)
    } else null

    val resultViewModel = if (resultViewModelFactory != null) {
        viewModel<ResultViewModel>(factory = resultViewModelFactory)
    } else {
        viewModel<ResultViewModel>()
    }

    // Load history otomatis saat user login/terdeteksi
    LaunchedEffect(userId) {
        if (userId > 0 && bmiRepository != null) resultViewModel.loadHistory(userId)
    }

    // --- NAVIGATION SETUP ---
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // SCREEN: SPLASH
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
                    }
                )
            }

            // SCREEN: HOME
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToResult = { summary ->
                        resultViewModel.setCurrentResult(summary)
                        navController.navigate(Screen.Result.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    inputViewModel = inputViewModel,
                    userViewModel = userViewModel,
                    reminderViewModel = reminderViewModel,

                    // TAMBAHAN: Oper ResultViewModel ke Home
                    resultViewModel = resultViewModel
                )
            }

            // SCREEN: RESULT
            composable(Screen.Result.route) {
                ResultScreen(
                    onNavigateBack = { navController.popBackStack() },
                    resultViewModel = resultViewModel,
                    userViewModel = userViewModel
                )
            }

            // SCREEN: HISTORY LIST
            composable(Screen.History.route) {
                HistoryScreen(
                    onNavigateToDetail = { navController.navigate(Screen.HistoryDetail.route) },
                    resultViewModel = resultViewModel,
                    userViewModel = userViewModel
                )
            }

            // SCREEN: HISTORY DETAIL
            composable(Screen.HistoryDetail.route) {
                HistoryDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    resultViewModel = resultViewModel
                )
            }

            // SCREEN: PROFILE
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToEdit = { navController.navigate(Screen.EditProfile.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    userViewModel = userViewModel
                )
            }

            // SCREEN: EDIT PROFILE
            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    userViewModel = userViewModel
                )
            }

            // SCREEN: SETTINGS
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPrivacy = { navController.navigate(Screen.PrivacyPolicy.route) }, // <-- Tambah ini
                    themeViewModel = themeViewModel,
                    reminderViewModel = reminderViewModel
                )
            }

            // SCREEN: PRIVACY POLICY
            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?,
    isDarkMode: Boolean
) {
    val containerColor = getNavBarContainerColor(isDarkMode)
    val selectedColor = BrandPrimary
    val unselectedColor = getNavBarUnselectedColor(isDarkMode)

    NavigationBar(
        containerColor = containerColor,
        modifier = Modifier.shadow(8.dp, spotColor = Color.Black.copy(alpha = 0.05f)),
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
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
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
                    indicatorColor = selectedColor.copy(alpha = 0.15f),
                    unselectedIconColor = unselectedColor,
                    unselectedTextColor = unselectedColor
                )
            )
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(Screen.Home.route, Icons.Rounded.Home, "Beranda")
    object History : BottomNavItem(Screen.History.route, Icons.Rounded.History, "Riwayat")
    object Profile : BottomNavItem(Screen.Profile.route, Icons.Rounded.Person, "Profil")
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Result : Screen("result")
    object History : Screen("history")
    object HistoryDetail : Screen("history_detail")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Settings : Screen("settings")
    object PrivacyPolicy : Screen("privacy_policy")
}