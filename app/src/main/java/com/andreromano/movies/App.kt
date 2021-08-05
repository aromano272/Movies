package com.andreromano.movies

import android.app.Application
import com.andreromano.movies.ui.di.dataModule
import com.andreromano.movies.ui.di.databaseModule
import com.andreromano.movies.ui.di.networkModule
import com.andreromano.movies.ui.di.uiModule
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        JodaTimeAndroid.init(this)

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                databaseModule,
                networkModule,
                dataModule,
                uiModule,
            )
        }
    }
}