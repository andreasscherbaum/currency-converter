package io.github.andreasscherbaum.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.andreasscherbaum.currencyconverter.data.preferences.PreferencesManager
import io.github.andreasscherbaum.currencyconverter.ui.about.AboutScreen
import io.github.andreasscherbaum.currencyconverter.ui.currencies.CurrenciesScreen
import io.github.andreasscherbaum.currencyconverter.ui.history.HistoryScreen
import io.github.andreasscherbaum.currencyconverter.ui.main.MainScreen
import io.github.andreasscherbaum.currencyconverter.ui.settings.SettingsScreen
import io.github.andreasscherbaum.currencyconverter.ui.theme.CurrencyConverterTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {
    val isDarkMode: StateFlow<Boolean> = preferencesManager.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainActivityViewModel = hiltViewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            CurrencyConverterTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToAbout = { navController.navigate("about") },
                            onNavigateToCurrencies = { navController.navigate("currencies") },
                            onNavigateToHistory = { navController.navigate("history") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable("about") {
                        AboutScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable("currencies") {
                        CurrenciesScreen(onNavigateBack = { navController.popBackStack() })
                    }
                    composable("history") {
                        HistoryScreen(onNavigateBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
