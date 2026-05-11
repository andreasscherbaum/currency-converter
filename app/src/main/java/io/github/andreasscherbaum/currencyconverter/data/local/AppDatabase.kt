package io.github.andreasscherbaum.currencyconverter.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.andreasscherbaum.currencyconverter.data.model.ConversionHistory
import io.github.andreasscherbaum.currencyconverter.data.model.Currency

@Database(
    entities = [Currency::class, ConversionHistory::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
    abstract fun conversionHistoryDao(): ConversionHistoryDao

    companion object {
        const val DATABASE_NAME = "currency_converter_db"
    }
}
