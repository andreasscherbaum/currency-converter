package io.github.andreasscherbaum.currencyconverter.data.repository

import io.github.andreasscherbaum.currencyconverter.data.local.ConversionHistoryDao
import io.github.andreasscherbaum.currencyconverter.data.local.CurrencyDao
import io.github.andreasscherbaum.currencyconverter.data.model.*
import io.github.andreasscherbaum.currencyconverter.data.parser.EcbXmlParser
import io.github.andreasscherbaum.currencyconverter.data.preferences.PreferencesManager
import io.github.andreasscherbaum.currencyconverter.data.remote.EcbApiService
import io.github.andreasscherbaum.currencyconverter.util.CurrencyNames
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val currencyDao: CurrencyDao,
    private val historyDao: ConversionHistoryDao,
    private val apiService: EcbApiService,
    private val xmlParser: EcbXmlParser,
    private val preferencesManager: PreferencesManager
) {

    private var cachedExchangeRates: ExchangeRates? = null
    private var lastRefreshTimestamp: String = ""
    private var lastDataDate: String = ""

    val allCurrencies: Flow<List<Currency>> = currencyDao.getAllCurrencies()
    val favoriteCurrencies: Flow<List<Currency>> = currencyDao.getFavoriteCurrencies()
    val conversionHistory: Flow<List<HistoryItem>> = historyDao.getRecentHistory().map { list ->
        list.map { it.toHistoryItem() }
    }

    suspend fun refreshRates(): Result<ExchangeRates> {
        return try {
            val xmlContent = apiService.getDailyRates()
            val exchangeRates = xmlParser.parse(xmlContent)
            cachedExchangeRates = exchangeRates
            lastRefreshTimestamp = formatCurrentTime()
            lastDataDate = exchangeRates.lastUpdated

            preferencesManager.setLastRefreshTime(lastRefreshTimestamp)
            preferencesManager.setLastDataDate(lastDataDate)

            // Update database
            val currencies = exchangeRates.rates.map { (code, rate) ->
                val existing = currencyDao.getCurrencyByCode(code)
                Currency(
                    code = code,
                    rate = rate,
                    name = CurrencyNames.getName(code),
                    isFavorite = existing?.isFavorite ?: false
                )
            }
            currencyDao.insertAll(currencies)

            Result.success(exchangeRates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCachedRates(): ExchangeRates? {
        if (cachedExchangeRates == null) {
            // Try to load from database
            val currencies = currencyDao.getAllCurrencies().first()
            if (currencies.isNotEmpty()) {
                lastRefreshTimestamp = preferencesManager.lastRefreshTime.first()
                lastDataDate = preferencesManager.lastDataDate.first()
                cachedExchangeRates = ExchangeRates(
                    baseCurrency = "EUR",
                    lastUpdated = lastDataDate,
                    rates = currencies.associate { it.code to it.rate }
                )
            }
        }
        return cachedExchangeRates
    }

    fun getLastUpdateTime(): String {
        return lastDataDate
    }

    fun getLastRefreshTime(): String {
        return lastRefreshTimestamp
    }

    suspend fun toggleFavorite(currencyCode: String) {
        val currency = currencyDao.getCurrencyByCode(currencyCode)
        currency?.let {
            currencyDao.updateFavorite(currencyCode, !it.isFavorite)
        }
    }

    suspend fun setFavorites(currencyCodes: Set<String>) {
        val allCurrencies = currencyDao.getAllCurrencies().first()
        allCurrencies.forEach { currency ->
            val shouldBeFavorite = currencyCodes.contains(currency.code)
            if (currency.isFavorite != shouldBeFavorite) {
                currencyDao.updateFavorite(currency.code, shouldBeFavorite)
            }
        }
    }

    fun convert(amount: Double, fromCurrency: String, toCurrency: String): Double {
        val rates = cachedExchangeRates?.rates ?: return 0.0
        val fromRate = rates[fromCurrency] ?: return 0.0
        val toRate = rates[toCurrency] ?: return 0.0

        // Convert to EUR first, then to target currency
        val eurAmount = amount / fromRate
        return eurAmount * toRate
    }

    suspend fun saveConversion(
        fromCurrency: String,
        fromAmount: Double,
        favoriteCurrencies: List<String>
    ) {
        val conversions = mutableMapOf<String, Double>()
        favoriteCurrencies.forEach { toCurrency ->
            conversions[toCurrency] = convert(fromAmount, fromCurrency, toCurrency)
        }

        val history = ConversionHistory(
            timestamp = System.currentTimeMillis(),
            fromCurrency = fromCurrency,
            fromAmount = fromAmount,
            conversions = JSONObject(conversions as Map<*, *>).toString(),
            lastUpdated = getLastUpdateTime()
        )

        historyDao.insert(history)
        historyDao.deleteOldEntries() // Keep only 25 most recent
    }

    private fun ConversionHistory.toHistoryItem(): HistoryItem {
        val conversionsMap = try {
            val jsonObject = JSONObject(conversions)
            jsonObject.keys().asSequence().associateWith { key ->
                jsonObject.getDouble(key)
            }
        } catch (e: Exception) {
            emptyMap()
        }

        return HistoryItem(
            id = id,
            timestamp = timestamp,
            fromCurrency = fromCurrency,
            fromAmount = fromAmount,
            conversions = conversionsMap,
            lastUpdated = lastUpdated
        )
    }

    private fun formatCurrentTime(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
