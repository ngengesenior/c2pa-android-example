package com.proofmode.c2pa

import android.app.Application
import com.proofmode.c2pa.utils.Constants
import com.proofmode.c2pa.utils.getOrGenerateKeyPair
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class C2PAApp: Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        getOrGenerateKeyPair(filesDir, BuildConfig.APPLICATION_ID)
        //Timber.d("onCreate: ${keyPairToPemStrings(keys)}")

    }
}