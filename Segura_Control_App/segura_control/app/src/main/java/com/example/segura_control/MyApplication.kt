// src/main/java/com/example/segura_control/MyApplication.kt
package com.example.segura_control

import android.app.Application
import android.util.Log
import androidx.work.Configuration // Asegúrate de tener este import

// Esta clase DEBE extender Application e implementar Configuration.Provider
class MyApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        // Aquí puedes poner otras inicializaciones que necesites para tu app.
        Log.d("MyApplication", "Application started. WorkManager will be initialized via Configuration.Provider.")
    }

    // ¡¡¡CORRECCIÓN AQUÍ!!!
    // DEBES implementar esta propiedad abstracta de Configuration.Provider
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            // Puedes personalizar la configuración aquí si lo necesitas
            // Por ejemplo, para depuración:
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
}