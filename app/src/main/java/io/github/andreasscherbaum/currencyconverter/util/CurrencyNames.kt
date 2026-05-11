package io.github.andreasscherbaum.currencyconverter.util

object CurrencyNames {
    private val names = mapOf(
        "EUR" to "Euro",
        "USD" to "US Dollar",
        "JPY" to "Japanese Yen",
        "BGN" to "Bulgarian Lev",
        "CZK" to "Czech Koruna",
        "DKK" to "Danish Krone",
        "GBP" to "British Pound",
        "HUF" to "Hungarian Forint",
        "PLN" to "Polish Zloty",
        "RON" to "Romanian Leu",
        "SEK" to "Swedish Krona",
        "CHF" to "Swiss Franc",
        "ISK" to "Icelandic Krona",
        "NOK" to "Norwegian Krone",
        "TRY" to "Turkish Lira",
        "AUD" to "Australian Dollar",
        "BRL" to "Brazilian Real",
        "CAD" to "Canadian Dollar",
        "CNY" to "Chinese Yuan",
        "HKD" to "Hong Kong Dollar",
        "IDR" to "Indonesian Rupiah",
        "ILS" to "Israeli Shekel",
        "INR" to "Indian Rupee",
        "KRW" to "South Korean Won",
        "MXN" to "Mexican Peso",
        "MYR" to "Malaysian Ringgit",
        "NZD" to "New Zealand Dollar",
        "PHP" to "Philippine Peso",
        "SGD" to "Singapore Dollar",
        "THB" to "Thai Baht",
        "ZAR" to "South African Rand"
    )

    fun getName(code: String): String {
        return names[code] ?: code
    }
}
