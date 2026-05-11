package io.github.andreasscherbaum.currencyconverter.data.local

import androidx.room.*
import io.github.andreasscherbaum.currencyconverter.data.model.ConversionHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversionHistoryDao {

    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC LIMIT 25")
    fun getRecentHistory(): Flow<List<ConversionHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: ConversionHistory)

    @Query("DELETE FROM conversion_history WHERE id NOT IN (SELECT id FROM conversion_history ORDER BY timestamp DESC LIMIT 25)")
    suspend fun deleteOldEntries()

    @Query("DELETE FROM conversion_history")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM conversion_history")
    suspend fun getCount(): Int
}
