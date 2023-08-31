package com.ethernet389.mai

import android.app.Application
import com.ethernet389.mai.di.appModule
import com.ethernet389.mai.di.dataModule
import com.ethernet389.mai.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(level = Level.DEBUG)
            androidContext(this@App)
            modules(appModule, domainModule, dataModule)
        }
    }
}