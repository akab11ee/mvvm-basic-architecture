package com.nukecare.di.modules

import android.content.Context
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import example.com.myapplication.BuildConfig
import example.com.myapplication.NetworkConstants.CACHE_SIZE_BYTES
import example.com.myapplication.NetworkConstants.CONNECTION_TIMEOUT
import example.com.myapplication.NetworkConstants.READ_TIMEOUT
import example.com.myapplication.NetworkConstants.WRITE_TIMEOUT
import example.com.myapplication.inteface.UserApi
import example.com.myapplication.interceptor.LoggerInterceptor
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@Suppress("unused")
object NetworkModule {
    /**
     * provides a custom OkHTPP object to be used a retrofit client
     * it could be used as a standalone http client
     */
    @Provides
    @Singleton
    @JvmStatic
    @Named("NukeCareClient")
    fun provideOkHttpSTCClient(
        @Named("loggingInterceptor") loggingInterceptor: Interceptor,
        cache: Cache,
        @Named("headerInterceptor") headerInterceptor: Interceptor
    ): OkHttpClient {

        val okHttpClientBuilder = OkHttpClient().newBuilder()
        okHttpClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.addInterceptor(loggingInterceptor)
        okHttpClientBuilder.cache(cache)
        return okHttpClientBuilder.build()
    }


    @Provides
    @Singleton
    @JvmStatic
    @Named("NukeCareClient")
    fun provideSTCRetrofit(
        @Named("NukeCareClient") client: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://thedigitalcare.com/apis/")
            .client(client)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @JvmStatic
    @Named("headerInterceptor")
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor {
            val requestBuilder = it.request().newBuilder()
            /* val date = getDate()
             val userAuthenticationToken = getUserAuthentication(userCredentials)
             val locationHeader = getLocationHeader(location, sharedPreferencesManager)
             requestBuilder.addHeader(HEADER_AUTH, getAuthentication(date))
             requestBuilder.addHeader(HEADER_DATE, date)
             requestBuilder.addHeader(HEADER_AGENT, getAgent())
             requestBuilder.addHeader(HEADER_ACCEPT, SharedPreferencesManager.getLanguage(context).language)
             requestBuilder.addHeader(HEADER_TEST, getTestHeader())
             requestBuilder.addHeader(HEADER_DEBUG_API_VERSION, "2")

             if (!userAuthenticationToken.isEmpty())
                 requestBuilder.addHeader(HEADER_USER, userAuthenticationToken)

             if (sharedPreferencesManager.isUserLogged() && !getLocationHeader(location, sharedPreferencesManager).isBlank())
                 requestBuilder.addHeader(HEADER_LOC, locationHeader)*/

//            if (NetworkCacheManager.isRequestInIgnoreCacheList(chain.request().url().toString())) {
//                //requestBuilder.removeHeader(HEADER_CACHE_CONTROL);
//                //requestBuilder.addHeader(HEADER_CACHE_CONTROL, NO_CACHE);
//                requestBuilder.cacheControl(CacheControl.FORCE_NETWORK)
//                NetworkCacheManager.removeRequestFromIgnoreList(chain.request().url().toString())
//            }

            it.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    @JvmStatic
    @Named("loggingInterceptor")
    fun provideLoggingInterceptor(): Interceptor {
        return LoggerInterceptor()
    }


    @Provides
    @Singleton
    @JvmStatic
    fun provideGsonConverter(): GsonConverterFactory {
        val gsonBuilder = GsonBuilder()
        return GsonConverterFactory.create(gsonBuilder.create())
    }


    @Provides
    @Singleton
    @JvmStatic
    internal fun provideCache(context: Context): Cache {
        val httpCacheDirectory = File(context.cacheDir.absolutePath, "HttpCache")
        return Cache(httpCacheDirectory, CACHE_SIZE_BYTES)
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideUserAPI(@Named("NukeCareClient") retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

}