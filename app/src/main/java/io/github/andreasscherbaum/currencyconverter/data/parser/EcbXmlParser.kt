package io.github.andreasscherbaum.currencyconverter.data.parser

import io.github.andreasscherbaum.currencyconverter.data.model.ExchangeRates
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

/**
 * Parser for ECB XML format
 * Example: https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml
 */
class EcbXmlParser {

    fun parse(xmlContent: String): ExchangeRates {
        val factory = XmlPullParserFactory.newInstance()
        factory.setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, false)
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xmlContent))

        var eventType = parser.eventType
        val rates = mutableMapOf<String, Double>()
        var lastUpdated = ""

        // EUR is always 1.0 as it's the base currency
        rates["EUR"] = 1.0

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "Cube" -> {
                            // Check for time attribute
                            val time = parser.getAttributeValue(null, "time")
                            if (time != null) {
                                lastUpdated = time
                            }

                            // Check for currency and rate attributes
                            val currency = parser.getAttributeValue(null, "currency")
                            val rate = parser.getAttributeValue(null, "rate")

                            if (currency != null && rate != null) {
                                try {
                                    rates[currency] = rate.toDouble()
                                } catch (e: NumberFormatException) {
                                    // Skip invalid rates
                                }
                            }
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        return ExchangeRates(
            baseCurrency = "EUR",
            lastUpdated = lastUpdated,
            rates = rates
        )
    }
}
