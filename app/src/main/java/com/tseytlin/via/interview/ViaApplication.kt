package com.tseytlin.via.interview

import android.app.Application
import com.tseytlin.via.interview.data.di.dataModule
import com.tseytlin.via.interview.detail.di.detailModule
import com.tseytlin.via.interview.home.di.homeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ViaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ViaApplication)
            modules(dataModule, homeModule, detailModule)
        }
    }
}
