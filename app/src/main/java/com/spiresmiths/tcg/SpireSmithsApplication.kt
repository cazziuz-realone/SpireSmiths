package com.spiresmiths.tcg

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for SpireSmiths TCG.
 * Entry point for Hilt dependency injection.
 */
@HiltAndroidApp
class SpireSmithsApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Future initialization code
    }
}