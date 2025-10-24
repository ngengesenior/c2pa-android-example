package com.proofmode.c2pa

import android.app.Application
import com.proofmode.c2pa.utils.generateOrGetKeyPair
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class C2PAApp: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        generateOrGetKeyPair(packageName)
    }
}