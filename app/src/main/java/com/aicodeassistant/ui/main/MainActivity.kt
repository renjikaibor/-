package com.aicodeassistant.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aicodeassistant.R
import com.aicodeassistant.ui.chat.ChatScreen
import com.aicodeassistant.ui.code.CodeScreen
import com.aicodeassistant.ui.settings.SettingsScreen
import com.aicodeassistant.ui.tools.ToolsScreen
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AICodeAssistant {
                val navController = rememberNavController()
                val currentTab by mainViewModel.currentTab.collectAsStateWithLifecycle(0)
                
                NavHost(navController, startDestination = MainDestinations.tabRoutes[currentTab].route) {
                    MainDestinations.tabRoutes.forEach { destination ->
                        composable(destination.route) {
                            when (destination) {
                                MainDestinations.Chat -> ChatScreen(navController)
                                MainDestinations.Code -> CodeScreen(navController)
                                MainDestinations.Tools -> ToolsScreen(navController)
                                MainDestinations.Settings -> SettingsScreen(navController)
                            }
                        }
                    }
                }
                
                BottomNavigationBar(navController, currentTab) { tab ->
                    mainViewModel.setCurrentTab(tab)
                }
            }
        }
    }
    
    private fun enableEdgeToEdge() {
        // Enable edge-to-edge display
        window.setDecorFitsSystemWindows(false)
    }
}

enum class MainDestinations(val route: String, val label: String, val icon: androidx.compose.material.icons.Icons.Outlined) {
    Chat("chat", "聊天", androidx.compose.material.icons.Icons.Outlined.Chat),
    Code("code", "代码", androidx.compose.material.icons.Icons.Outlined.Code),
    Tools("tools", "工具", androidx.compose.material.icons.Icons.Outlined.Build),
    Settings("settings", "设置", androidx.compose.material.icons.Icons.Outlined.Settings);

    companion object {
        val tabRoutes = values()
    }
}

@Composable
fun BottomNavigationBar(
    navController: androidx.navigation.NavController,
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    androidx.compose.material3.NavigationBar {
        MainDestinations.tabRoutes.forEachIndexed { index, destination ->
            androidx.compose.material3.NavigationBarItem(
                selected = currentTab == index,
                onClick = { onTabSelected(index) },
                icon = {
                    androidx.compose.material.Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = { androidx.compose.material3.Text(destination.label) },
                alwaysShowLabel = true
            )
        }
    }
}
