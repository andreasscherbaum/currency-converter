package io.github.andreasscherbaum.currencyconverter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a currency with its exchange rate against EUR
 */
@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey
    val code: String,
    val rate: Double,
    val name: String,
    val isFavorite: Boolean = false
)

/**
 * Currency exchange rates data from ECB
 */
data class ExchangeRates(
    val baseCurrency: String = "EUR",
    val lastUpdated: String,
    val rates: Map<String, Double>
)

/**
 * Conversion result for display
 */
data class ConversionResult(
    val fromCurrency: String,
    val toCurrency: String,
    val fromAmount: Double,
    val toAmount: Double,
    val rate: Double
)
