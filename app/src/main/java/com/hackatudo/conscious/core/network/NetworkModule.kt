package com.hackatudo.conscious.core.network

import com.hackatudo.conscious.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun provideHttpClient(auth: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(auth).build()

    @Provides @Singleton fun provideSharedApi(client: OkHttpClient): SharedApi {
        val baseUrl = BuildConfig.API_BASE_URL.toHttpUrl()
        require(baseUrl.isHttps) { "Remote API must use HTTPS" }
        return Retrofit.Builder().baseUrl(baseUrl).client(client).addConverterFactory(GsonConverterFactory.create()).build().create(SharedApi::class.java)
    }
}
