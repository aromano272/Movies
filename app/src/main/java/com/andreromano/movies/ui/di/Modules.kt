package com.andreromano.movies.ui.di

import androidx.room.Room
import androidx.room.withTransaction
import com.andreromano.movies.BuildConfig
import com.andreromano.movies.data.ApiConfigurationRepository
import com.andreromano.movies.data.MoviesRepository
import com.andreromano.movies.database.*
import com.andreromano.movies.database.mapper.RoomTypeConverters
import com.andreromano.movies.domain.model.Movie
import com.andreromano.movies.network.Api
import com.andreromano.movies.network.mapper.FromBaseResponseToResultKtAdapterFactory
import com.andreromano.movies.network.mapper.ResultKtCallAdapterFactory
import com.andreromano.movies.ui.screens.movie_details.MovieDetailsViewModel
import com.andreromano.movies.ui.screens.movie_list.MovieListViewModel
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber

val databaseModule = module {

    single<PreferenceStorage> {
        SharedPreferenceStorage(get(), get())
    }

    single<AppDatabase> {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "MoviesDB"
        )
            .addTypeConverter(RoomTypeConverters(get()))
            .fallbackToDestructiveMigration()
            .build()
    }

    single<TransactionRunner> {
        val db = get<AppDatabase>()
        object : TransactionRunner {
            override suspend fun run(block: suspend () -> Unit) {
                db.withTransaction { block() }
            }
        }
    }

    factory<MoviesDao> { get<AppDatabase>().moviesDao() }
    factory<MovieOrderByFilterDao> { get<AppDatabase>().movieOrderByFilterDao() }
    factory<MovieDetailsDao> { get<AppDatabase>().movieDetailsDao() }
    factory<MovieWithRecommendationJoinDao> { get<AppDatabase>().movieWithRecommendationJoinDao() }
    factory<MovieFavoriteDao> { get<AppDatabase>().movieFavoriteDao() }

    single<Moshi> {
        Moshi.Builder()
            .build()
    }

}

val networkModule = module {

    single<Api> {
        val retrofitBuilder =
            Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(get()))
                .addCallAdapterFactory(ResultKtCallAdapterFactory())

        val httpBuilder = OkHttpClient.Builder()

        // Add Api key to all requests
        httpBuilder.addInterceptor { chain ->
            val original = chain.request()
            val newHttpUrl = original.url.newBuilder()
                .addQueryParameter("api_key", BuildConfig.API_KEY)
                .build()

            chain.proceed(
                original
                    .newBuilder()
                    .url(newHttpUrl)
                    .build()
            )
        }

        httpBuilder.addInterceptor(
            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Timber.tag("OkHttp").d(message)
                }
            }).apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )

        val retrofit = retrofitBuilder
            .client(httpBuilder.build())
            .build()

        retrofit.create(Api::class.java)
    }

}

val dataModule = module {

    single<MoviesRepository> {
        MoviesRepository(get(), get(), get(), get(), get(), get(), get(), get())
    }

    single<ApiConfigurationRepository> {
        ApiConfigurationRepository(get(), get())
    }

    single<Moshi> {
        Moshi.Builder()
            .add(FromBaseResponseToResultKtAdapterFactory())
            .build()
    }

}

val uiModule = module {

    viewModel<MovieListViewModel> {
        MovieListViewModel(get())
    }

    viewModel<MovieDetailsViewModel> { (initialMovie: Movie) ->
        MovieDetailsViewModel(initialMovie, get())
    }

}