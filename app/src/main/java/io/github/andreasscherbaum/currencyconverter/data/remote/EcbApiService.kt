package io.github.andreasscherbaum.currencyconverter.data.remote

import retrofit2.http.GET

interface EcbApiService {

    @GET("eurofxref-daily.xml")
    suspend fun getDailyRates(): String

    companion object {
        const val BASE_URL = "https://www.ecb.europa.eu/stats/eurofxref/"
    }
}
