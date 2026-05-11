package io.github.andreasscherbaum.currencyconverter.di

import android.content.Context
import androidx.room.Room
import io.github.andreasscherbaum.currencyconverter.data.local.AppDatabase
import io.github.andreasscherbaum.currencyconverter.data.local.ConversionHistoryDao
import io.github.andreasscherbaum.currencyconverter.data.local.CurrencyDao
import io.github.andreasscherbaum.currencyconverter.data.parser.EcbXmlParser
import io.github.andreasscherbaum.currencyconverter.data.remote.EcbApiService
import io.github.andreasscherbaum.currencyconverter.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(database: AppDatabase): CurrencyDao {
        return database.currencyDao()
    }

    @Provides
    @Singleton
    fun provideConversionHistoryDao(database: AppDatabase): ConversionHistoryDao {
        return database.conversionHistoryDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                    addInterceptor(logging)
                }
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideEcbApiService(okHttpClient: OkHttpClient): EcbApiService {
        return Retrofit.Builder()
            .baseUrl(EcbApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(EcbApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEcbXmlParser(): EcbXmlParser {
        return EcbXmlParser()
    }
}
