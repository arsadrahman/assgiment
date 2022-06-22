package com.app.pepuldemo.di

import android.content.Context
import com.app.pepuldemo.utility.Constants
import com.iceteck.silicompressorr.SiliCompressor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CompressorModule {
    @Singleton
    @Provides
    fun provideSiliCompressor(@ApplicationContext context: Context): SiliCompressor = SiliCompressor.with(context)

}