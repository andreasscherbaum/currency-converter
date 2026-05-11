package io.github.andreasscherbaum.currencyconverter.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.andreasscherbaum.currencyconverter.data.model.Currency
import io.github.andreasscherbaum.currencyconverter.data.preferences.PreferencesManager
import io.github.andreasscherbaum.currencyconverter.data.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _inputAmount = MutableStateFlow("")
    val inputAmount: StateFlow<String> = _inputAmount.asStateFlow()

    private val _confirmedAmount = MutableStateFlow("")
    val confirmedAmount: StateFlow<String> = _confirmedAmount.asStateFlow()

    private val _selectedCurrency = MutableStateFlow("EUR")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage.asStateFlow()

    private val _showDownloadPrompt = MutableStateFlow(false)
    val showDownloadPrompt: StateFlow<Boolean> = _showDownloadPrompt.asStateFlow()

    val allCurrencies: StateFlow<List<Currency>> = repository.allCurrencies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteCurrencies: StateFlow<List<Currency>> = repository.favoriteCurrencies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteCurrencyCodes: StateFlow<Set<String>> = preferencesManager.favoriteCurrencies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _lastUpdateTime = MutableStateFlow("")
    val lastUpdateTime: StateFlow<String> = _lastUpdateTime.asStateFlow()

    private val _lastRefreshTime = MutableStateFlow("")
    val lastRefreshTime: StateFlow<String> = _lastRefreshTime.asStateFlow()

    init {
        viewModelScope.launch {
            // Load last selected currency
            preferencesManager.lastCurrency.collect { currency ->
                _selectedCurrency.value = currency
            }
        }

        viewModelScope.launch {
            val cached = repository.getCachedRates()
            if (cached != null) {
                _lastUpdateTime.value = repository.getLastUpdateTime()
                _lastRefreshTime.value = repository.getLastRefreshTime()
            } else {
                _showDownloadPrompt.value = true
            }
        }
    }

    fun onNumberClick(number: String) {
        val current = _inputAmount.value
        _inputAmount.value = when {
            current == "0" && number != "." -> number
            current.contains(".") && number == "." -> current
            else -> current + number
        }
    }

    fun onDeleteClick() {
        val current = _inputAmount.value
        _inputAmount.value = if (current.isNotEmpty()) {
            current.dropLast(1)
        } else {
            ""
        }
    }

    fun onClearClick() {
        _inputAmount.value = ""
        _confirmedAmount.value = ""
    }

    fun onDecimalClick() {
        val current = _inputAmount.value
        if (current.isEmpty()) {
            _inputAmount.value = "0."
        } else if (!current.contains(".")) {
            _inputAmount.value = "$current."
        }
    }

    fun onEnterClick() {
        val amount = _inputAmount.value.toDoubleOrNull() ?: return
        if (amount <= 0) return
        _confirmedAmount.value = _inputAmount.value
        saveConversionToHistory()
    }

    fun onCurrencySelected(currency: String) {
        _selectedCurrency.value = currency
        viewModelScope.launch {
            preferencesManager.setLastCurrency(currency)
        }
    }

    fun convertAmount(toCurrency: String, amount: Double): Double {
        return repository.convert(amount, _selectedCurrency.value, toCurrency)
    }

    fun dismissDownloadPrompt() {
        _showDownloadPrompt.value = false
    }

    fun acceptDownloadPrompt() {
        _showDownloadPrompt.value = false
        updateRates()
    }

    fun updateRates() {
        viewModelScope.launch {
            _isUpdating.value = true
            _updateMessage.value = null

            repository.refreshRates()
                .onSuccess {
                    _lastUpdateTime.value = repository.getLastUpdateTime()
                    _lastRefreshTime.value = repository.getLastRefreshTime()
                    _updateMessage.value = "Rates updated"
                    repository.setFavorites(favoriteCurrencyCodes.value)
                }
                .onFailure {
                    _updateMessage.value = "Update failed"
                }

            _isUpdating.value = false

            // Clear message after 3 seconds
            kotlinx.coroutines.delay(3000)
            _updateMessage.value = null
        }
    }

    fun saveConversionToHistory() {
        val amount = _inputAmount.value.toDoubleOrNull() ?: return
        if (amount <= 0) return

        viewModelScope.launch {
            val favorites = favoriteCurrencyCodes.value.toList()
            repository.saveConversion(_selectedCurrency.value, amount, favorites)
        }
    }

}
