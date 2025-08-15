package com.example.segura_control

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log // <--- Importar la clase Log aquí
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.segura_control.sheets.Rutas
import com.google.gson.Gson
import com.segura.Activity.RutasActivity
import java.util.concurrent.TimeUnit

class RutasUpdateWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val CHANNEL_ID = "rutas_notification_channel"
    private val NOTIFICATION_ID = 101

    // SOLO DEBE HABER UNA FUNCIÓN doWork()
    override suspend fun doWork(): Result {
        Log.d("WorkerDebug", "RutasUpdateWorker: Iniciando doWork()") // Log de inicio
        return try {
            val response = RetrofitClient.webService(BaseUrl.base_url_get).obtenerTodoRutas()
            val nextWorkRequest = OneTimeWorkRequestBuilder<RutasUpdateWorker>()
                .setInitialDelay(30, TimeUnit.SECONDS) // Retraso para la siguiente ejecución
                .build()

            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                "RutasUpdaterWork",
                ExistingWorkPolicy.REPLACE,
                nextWorkRequest
            )

            if (response.isSuccessful) {
                Log.d("WorkerDebug", "RutasUpdateWorker: Respuesta de API exitosa") // Log de respuesta exitosa
                val nuevasRutas = response.body()?.rutas ?: emptyList()

                // Obtener datos guardados previamente
                val sharedPrefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val lastKnownRutasJson = sharedPrefs.getString("last_known_rutas", null)

                val gson = Gson()
                val lastKnownRutas = if (lastKnownRutasJson != null) {
                    gson.fromJson(lastKnownRutasJson, Array<Rutas>::class.java).toList()
                } else {
                    emptyList()
                }

                // Comparar si los datos han cambiado
                // Una comparación simple de si la lista actual es diferente a la última conocida.
                if (nuevasRutas != lastKnownRutas) {
                    Log.d("WorkerDebug", "RutasUpdateWorker: Datos cambiaron. Enviando notificación.") // Log si hay cambios
                    // Hay datos nuevos o modificados, guardar la nueva versión y enviar notificación
                    sharedPrefs.edit().putString("last_known_rutas", gson.toJson(nuevasRutas)).apply()
                    sendNotification("Actualización de Rutas", "Hay nuevos datos de rutas disponibles.")
                } else {
                    Log.d("WorkerDebug", "RutasUpdateWorker: No hay cambios en los datos.") // Log si no hay cambios
                }

                Result.success()
            } else {
                Log.e("WorkerDebug", "RutasUpdateWorker: Error en la respuesta de la API: ${response.code()}") // Log de error de API
                Result.retry() // Reintentar si la llamada a la API falla temporalmente
            }
        } catch (e: Exception) {
            Log.e("WorkerDebug", "RutasUpdateWorker: Excepción durante el trabajo: ${e.message}", e) // Log de excepción
            Result.failure() // Fallo permanente si hay una excepción no esperada
        }
    }


    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear un canal de notificación para Android Oreo (API 26) y superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones de Rutas",
                NotificationManager.IMPORTANCE_HIGH // Puedes cambiar a IMPORTANCE_HIGH si quieres que suene o vibre por defecto
            ).apply {
                description = "Canal para notificaciones de actualizaciones de rutas"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la actividad principal cuando se toca la notificación
        val intent = Intent(applicationContext, RutasActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE es requerido para API 23+
        )

        // Construir la notificación
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Asegúrate de tener un icono de notificación válido aquí
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // O PRIORITY_HIGH para mayor visibilidad
            .setContentIntent(pendingIntent) // Establecer el PendingIntent
            .setAutoCancel(true) // La notificación se cierra cuando se toca
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
        Log.d("WorkerDebug", "RutasUpdateWorker: Notificación enviada.") // Log después de enviar la notificación
    }
}