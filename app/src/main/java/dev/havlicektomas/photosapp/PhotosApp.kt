package dev.havlicektomas.photosapp

import android.app.Application
import dev.havlicektomas.photosapp.core.di.coreNetworkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class PhotosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@PhotosApp)
            modules(coreNetworkModule)
        }
    }
}
