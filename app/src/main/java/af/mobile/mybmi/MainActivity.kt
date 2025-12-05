package af.mobile.mybmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint

// --- IMPORT SCREENS ---
import af.mobile.mybmi.screens.history.HistoryDetailScreen
import af.mobile.mybmi.screens.history.HistoryScreen
import af.mobile.mybmi.screens.home.HomeScreen
import af.mobile.mybmi.screens.profile.ProfileScreen
import af.mobile.mybmi.screens.result.ResultScreen
import af.mobile.mybmi.screens.settings.SettingsScreen
import af.mobile.mybmi.screens.splash.SplashScreen
import af.mobile.mybmi.screens.profile.EditProfileScreen
import af.mobile.mybmi.screens.profile.BadgeListScreen
import af.mobile.mybmi.screens.settings.GuideScreen
import af.mobile.mybmi.screens.settings.PrivacyPolicyScreen
import af.mobile.mybmi.screens.settings.TermsScreen
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.viewmodel.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()

            // Setup Theme
            val systemInDarkTheme = isSystemInDarkTheme()
            LaunchedEffect(Unit) {
                themeViewModel.initializeTheme(systemInDarkTheme)
            }
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            myBMITheme(isDarkMode = isDarkMode) {
                MyBMIApp(themeViewModel = themeViewModel)
            }
        }
    }
}

@Composable
fun MyBMIApp(
    themeViewModel: ThemeViewModel
) {
    val navController = rememberNavController()

    val inputViewModel: InputViewModel = hiltViewModel()
    val reminderViewModel: ReminderViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val resultViewModel: ResultViewModel = hiltViewModel()

    val currentUser by userViewModel.currentUser.collectAsState()
    val userId = currentUser?.id ?: 0

    LaunchedEffect(userId) {
        if (userId > 0) {
            resultViewModel.loadHistory(userId)
        }
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
            modifier = Modifier.padding(paddingValues),

            // --- ANIMASI TRANSISI LAYAR (SLIDE) ---
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500))
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500))
            }
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
                        resultViewModel.selectHistory(summary)

                        navController.navigate(Screen.Result.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    inputViewModel = inputViewModel,
                    userViewModel = userViewModel,
                    reminderViewModel = reminderViewModel,
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
                    onNavigateToBadgeList = { navController.navigate(Screen.BadgeList.route) },
                    userViewModel = userViewModel
                )
            }

            // SCREEN: BADGE LIST
            composable(Screen.BadgeList.route) {
                BadgeListScreen(
                    onNavigateBack = { navController.popBackStack() },
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
                    onNavigateToPrivacy = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onNavigateToTerms = { navController.navigate(Screen.Terms.route) },
                    onNavigateToGuide = { navController.navigate(Screen.Guide.route) },
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

            // SCREEN: TERMS
            composable(Screen.Terms.route) {
                TermsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // SCREEN: GUIDE
            composable(Screen.Guide.route) {
                GuideScreen(
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
                        contentDescription = stringResource(item.labelRes),
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.labelRes),
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

// SEALED CLASS
sealed class BottomNavItem(val route: String, val icon: ImageVector, val labelRes: Int) {
    object Home : BottomNavItem(Screen.Home.route, Icons.Rounded.Home, R.string.nav_home)
    object History : BottomNavItem(Screen.History.route, Icons.Rounded.BarChart, R.string.nav_history)
    object Profile : BottomNavItem(Screen.Profile.route, Icons.Rounded.Person, R.string.nav_profile)
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Result : Screen("result")
    object History : Screen("history")
    object HistoryDetail : Screen("history_detail")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object BadgeList : Screen("badge_list")
    object Settings : Screen("settings")
    object PrivacyPolicy : Screen("privacy_policy")
    object Terms : Screen("terms")
    object Guide : Screen("guide")
}