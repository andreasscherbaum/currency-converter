package io.github.andreasscherbaum.currencyconverter.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * History entry for currency conversions
 */
@Entity(tableName = "conversion_history")
data class ConversionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val fromCurrency: String,
    val fromAmount: Double,
    val conversions: String, // JSON string of Map<String, Double> for destination currencies
    val lastUpdated: String // ECB data timestamp at time of conversion
)

/**
 * Display model for history item
 */
data class HistoryItem(
    val id: Long,
    val timestamp: Long,
    val fromCurrency: String,
    val fromAmount: Double,
    val conversions: Map<String, Double>,
    val lastUpdated: String
)
