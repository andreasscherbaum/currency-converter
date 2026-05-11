package io.github.andreasscherbaum.currencyconverter.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.andreasscherbaum.currencyconverter.R
import io.github.andreasscherbaum.currencyconverter.data.model.Currency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToCurrencies: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val inputAmount by viewModel.inputAmount.collectAsState()
    val confirmedAmount by viewModel.confirmedAmount.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val allCurrencies by viewModel.allCurrencies.collectAsState()
    val favoriteCurrencyCodes by viewModel.favoriteCurrencyCodes.collectAsState()
    val isUpdating by viewModel.isUpdating.collectAsState()
    val updateMessage by viewModel.updateMessage.collectAsState()
    val lastUpdateTime by viewModel.lastUpdateTime.collectAsState()
    val lastRefreshTime by viewModel.lastRefreshTime.collectAsState()
    val showDownloadPrompt by viewModel.showDownloadPrompt.collectAsState()

    var showCurrencyPicker by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_settings)) },
                            onClick = {
                                showMenu = false
                                onNavigateToSettings()
                            },
                            leadingIcon = { Icon(Icons.Default.Settings, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_currencies)) },
                            onClick = {
                                showMenu = false
                                onNavigateToCurrencies()
                            },
                            leadingIcon = { Icon(Icons.Default.AttachMoney, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_history)) },
                            onClick = {
                                showMenu = false
                                onNavigateToHistory()
                            },
                            leadingIcon = { Icon(Icons.Default.History, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_about)) },
                            onClick = {
                                showMenu = false
                                onNavigateToAbout()
                            },
                            leadingIcon = { Icon(Icons.Default.Info, null) }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Currency selector and input display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Currency selector
                Button(
                    onClick = { showCurrencyPicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(selectedCurrency, fontSize = 18.sp)
                    Icon(Icons.Default.ArrowDropDown, null)
                }

                // Amount display
                Text(
                    text = inputAmount.ifEmpty { "0" },
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.weight(2f),
                    textAlign = TextAlign.End
                )
            }

            // Calculator buttons
            CalculatorGrid(
                onNumberClick = { viewModel.onNumberClick(it) },
                onDeleteClick = { viewModel.onDeleteClick() },
                onClearClick = { viewModel.onClearClick() },
                onDecimalClick = { viewModel.onDecimalClick() },
                onEnterClick = { viewModel.onEnterClick() },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Favorite currencies list
            Text(
                text = stringResource(R.string.favorite_currencies),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val displayedFavorites = allCurrencies.filter { it.code in favoriteCurrencyCodes }
                items(displayedFavorites, key = { it.code }) { currency ->
                    val amount = confirmedAmount.toDoubleOrNull() ?: 0.0
                    ConversionResultItem(
                        currency = currency,
                        convertedAmount = viewModel.convertAmount(currency.code, amount)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Update button
            UpdateRatesButton(
                lastUpdateTime = lastUpdateTime,
                lastRefreshTime = lastRefreshTime,
                isUpdating = isUpdating,
                updateMessage = updateMessage,
                onUpdateClick = { viewModel.updateRates() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    // Currency picker dialog
    if (showCurrencyPicker) {
        val sortedCurrencies = allCurrencies.sortedWith(
            compareByDescending<Currency> { it.code in favoriteCurrencyCodes }
                .thenBy { it.code }
        )
        CurrencyPickerDialog(
            currencies = sortedCurrencies,
            onDismiss = { showCurrencyPicker = false },
            onSelect = { currency ->
                viewModel.onCurrencySelected(currency.code)
                showCurrencyPicker = false
            }
        )
    }

    // Download prompt dialog
    if (showDownloadPrompt) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDownloadPrompt() },
            title = { Text(stringResource(R.string.download_prompt_title)) },
            text = { Text(stringResource(R.string.download_prompt_message)) },
            confirmButton = {
                TextButton(onClick = { viewModel.acceptDownloadPrompt() }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDownloadPrompt() }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }

}

@Composable
fun CalculatorGrid(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit,
    onDecimalClick: () -> Unit,
    onEnterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Row 1: 7 8 9 DEL
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton("7", Modifier.weight(1f)) { onNumberClick("7") }
            CalculatorButton("8", Modifier.weight(1f)) { onNumberClick("8") }
            CalculatorButton("9", Modifier.weight(1f)) { onNumberClick("9") }
            CalculatorButton("⌫", Modifier.weight(1f)) { onDeleteClick() }
        }
        // Row 2: 4 5 6 AC
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton("4", Modifier.weight(1f)) { onNumberClick("4") }
            CalculatorButton("5", Modifier.weight(1f)) { onNumberClick("5") }
            CalculatorButton("6", Modifier.weight(1f)) { onNumberClick("6") }
            CalculatorButton("AC", Modifier.weight(1f)) { onClearClick() }
        }
        // Row 3: 1 2 3 Enter
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton("1", Modifier.weight(1f)) { onNumberClick("1") }
            CalculatorButton("2", Modifier.weight(1f)) { onNumberClick("2") }
            CalculatorButton("3", Modifier.weight(1f)) { onNumberClick("3") }
            CalculatorButton("⏎", Modifier.weight(1f)) { onEnterClick() }
        }
        // Row 4: 0 .
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton("0", Modifier.weight(2f)) { onNumberClick("0") }
            CalculatorButton(".", Modifier.weight(1f)) { onDecimalClick() }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ConversionResultItem(
    currency: Currency,
    convertedAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = currency.code,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currency.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Text(
                text = String.format("%.2f", convertedAmount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun UpdateRatesButton(
    lastUpdateTime: String,
    lastRefreshTime: String,
    isUpdating: Boolean,
    updateMessage: String?,
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(enabled = !isUpdating) { onUpdateClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isUpdating) {
                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.updating), style = MaterialTheme.typography.bodyMedium)
            } else {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.update_rates), style = MaterialTheme.typography.bodyMedium)
            }
        }

        if (lastRefreshTime.isNotEmpty() && !isUpdating) {
            Text(
                text = "Last refresh: $lastRefreshTime",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        updateMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                color = if (it.contains("failed")) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CurrencyPickerDialog(
    currencies: List<Currency>,
    onDismiss: () -> Unit,
    onSelect: (Currency) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.from_currency)) },
        text = {
            LazyColumn {
                items(currencies) { currency ->
                    ListItem(
                        headlineContent = { Text(currency.code) },
                        supportingContent = { Text(currency.name) },
                        modifier = Modifier.clickable { onSelect(currency) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
