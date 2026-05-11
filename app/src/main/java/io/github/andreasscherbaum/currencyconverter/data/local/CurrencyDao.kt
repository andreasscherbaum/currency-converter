package io.github.andreasscherbaum.currencyconverter.data.local

import androidx.room.*
import io.github.andreasscherbaum.currencyconverter.data.model.Currency
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currencies ORDER BY code ASC")
    fun getAllCurrencies(): Flow<List<Currency>>

    @Query("SELECT * FROM currencies WHERE isFavorite = 1 ORDER BY code ASC")
    fun getFavoriteCurrencies(): Flow<List<Currency>>

    @Query("SELECT * FROM currencies WHERE code = :code")
    suspend fun getCurrencyByCode(code: String): Currency?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<Currency>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: Currency)

    @Update
    suspend fun update(currency: Currency)

    @Query("UPDATE currencies SET isFavorite = :isFavorite WHERE code = :code")
    suspend fun updateFavorite(code: String, isFavorite: Boolean)

    @Query("DELETE FROM currencies")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM currencies")
    suspend fun getCount(): Int
}
