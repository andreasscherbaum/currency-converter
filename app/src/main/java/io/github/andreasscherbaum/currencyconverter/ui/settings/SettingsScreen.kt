package io.github.andreasscherbaum.currencyconverter.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.andreasscherbaum.currencyconverter.R
import io.github.andreasscherbaum.currencyconverter.data.model.Currency
import io.github.andreasscherbaum.currencyconverter.data.preferences.PreferencesManager
import io.github.andreasscherbaum.currencyconverter.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val repository: CurrencyRepository
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = preferencesManager.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val language: StateFlow<String> = preferencesManager.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val allCurrencies: StateFlow<List<Currency>> = repository.allCurrencies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteCurrencies: StateFlow<Set<String>> = preferencesManager.favoriteCurrencies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(enabled)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
        }
    }

    fun toggleFavoriteCurrency(currencyCode: String) {
        viewModelScope.launch {
            val current = favoriteCurrencies.value.toMutableSet()
            if (current.contains(currencyCode)) {
                current.remove(currencyCode)
            } else {
                current.add(currencyCode)
            }
            preferencesManager.setFavoriteCurrencies(current)
            repository.setFavorites(current)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val allCurrencies by viewModel.allCurrencies.collectAsState()
    val favoriteCurrencies by viewModel.favoriteCurrencies.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFavoritesDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_dark_mode)) },
                    trailingContent = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { viewModel.setDarkMode(it) }
                        )
                    }
                )
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_language)) },
                    supportingContent = {
                        Text(
                            when (language) {
                                "en" -> stringResource(R.string.language_english)
                                "de" -> stringResource(R.string.language_german)
                                else -> stringResource(R.string.language_system)
                            }
                        )
                    },
                    modifier = Modifier.clickable { showLanguageDialog = true }
                )
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.settings_favorite_currencies)) },
                    supportingContent = { Text("${favoriteCurrencies.size} selected") },
                    modifier = Modifier.clickable { showFavoritesDialog = true }
                )
            }
        }
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.settings_language)) },
            text = {
                Column {
                    listOf(
                        "system" to R.string.language_system,
                        "en" to R.string.language_english,
                        "de" to R.string.language_german
                    ).forEach { (code, labelRes) ->
                        ListItem(
                            headlineContent = { Text(stringResource(labelRes)) },
                            trailingContent = {
                                if (language == code) {
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }
                            },
                            modifier = Modifier.clickable {
                                viewModel.setLanguage(code)
                                showLanguageDialog = false
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    if (showFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showFavoritesDialog = false },
            title = { Text(stringResource(R.string.settings_favorite_currencies)) },
            text = {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    items(allCurrencies) { currency ->
                        val isChecked = favoriteCurrencies.contains(currency.code)
                        ListItem(
                            headlineContent = { Text(currency.code) },
                            supportingContent = { Text(currency.name) },
                            trailingContent = {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { viewModel.toggleFavoriteCurrency(currency.code) }
                                )
                            },
                            modifier = Modifier.clickable {
                                viewModel.toggleFavoriteCurrency(currency.code)
                            }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFavoritesDialog = false }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}
