package io.github.andreasscherbaum.currencyconverter.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferenceKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val LAST_CURRENCY = stringPreferencesKey("last_currency")
        val FAVORITE_CURRENCIES = stringSetPreferencesKey("favorite_currencies")
        val LAST_REFRESH_TIME = stringPreferencesKey("last_refresh_time")
        val LAST_DATA_DATE = stringPreferencesKey("last_data_date")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DARK_MODE] ?: false
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LANGUAGE] ?: "system"
    }

    val lastCurrency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LAST_CURRENCY] ?: "EUR"
    }

    val favoriteCurrencies: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.FAVORITE_CURRENCIES] ?: setOf("EUR", "CAD", "PLN", "CZK")
    }

    val lastRefreshTime: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LAST_REFRESH_TIME] ?: ""
    }

    val lastDataDate: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LAST_DATA_DATE] ?: ""
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DARK_MODE] = enabled
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LANGUAGE] = language
        }
    }

    suspend fun setLastCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_CURRENCY] = currency
        }
    }

    suspend fun setFavoriteCurrencies(currencies: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.FAVORITE_CURRENCIES] = currencies
        }
    }

    suspend fun setLastRefreshTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_REFRESH_TIME] = time
        }
    }

    suspend fun setLastDataDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_DATA_DATE] = date
        }
    }
}
