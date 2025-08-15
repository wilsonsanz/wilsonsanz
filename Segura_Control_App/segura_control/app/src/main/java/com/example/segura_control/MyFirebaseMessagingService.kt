package com.example.segura_control

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.segura_control.sheets.Rutas
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.segura.Activity.RutasActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "rutas_fcm_channel"
    private val NOTIFICATION_ID = 102 // ID único para notificaciones FCM

    /**
     * Se llama cuando un nuevo token de registro para una instancia de aplicación está disponible.
     * Esto ocurre cuando la app se instala, el usuario restaura el dispositivo o la app,
     * o el token expira y se refresca.
     */
    override fun onNewToken(token: String) {
        Log.d("FCM_TOKEN", "Nuevo token: $token")
        // **IMPORTANTE:** Debes enviar este 'token' a tu servidor (backend)
        // para que tu servidor pueda enviar mensajes a este dispositivo específico.
        // Implementa aquí la lógica para enviar el token a tu servidor.
        sendRegistrationToServer(token)
    }

    /**
     * Se llama cuando se recibe un mensaje FCM.
     * Puede contener una carga útil de datos o de notificación (o ambas).
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM_MESSAGE", "Mensaje recibido de: ${remoteMessage.from}")

        // 1. Manejar mensajes de datos
        // Si tu servidor envía datos para que la app los procese internamente
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM_MESSAGE", "Carga útil de datos del mensaje: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }

        // 2. Manejar mensajes de notificación
        // Si tu servidor envía una notificación visible directamente
        remoteMessage.notification?.let {
            Log.d("FCM_MESSAGE", "Carga útil de notificación del mensaje: Título: ${it.title} / Cuerpo: ${it.body}")
            // Muestra una notificación usando el título y cuerpo del mensaje
            sendNotification(it.title ?: "Nueva Actualización", it.body ?: "Hay nuevos datos disponibles.")
        }
    }

    /**
     * Lógica para enviar el token de registro a tu servidor.
     * **DEBES IMPLEMENTAR ESTO** para que tu servidor sepa a qué dispositivos enviar notificaciones.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Aquí debes hacer una llamada a tu API/servidor para enviar el token.
        // Por ejemplo, asociar este token a un ID de usuario en tu base de datos.
        // val userId = "id_del_usuario_actual" // Obtén el ID de usuario si lo tienes
        // RetrofitClient.webService(BaseUrl.your_server_url).sendFcmToken(userId, token)
        Log.d("FCM_TOKEN", "Simulando envío de token a tu servidor: $token")
    }

    /**
     * Procesa la carga útil de datos del mensaje FCM.
     * Aquí es donde podrías comparar las rutas y decidir si notificar al usuario.
     */
    private fun handleDataMessage(data: Map<String, String>) {
        // Ejemplo: si tu servidor envía las rutas completas como JSON en el campo "rutas_data"
        val rutasJson = data["rutas_data"]
        rutasJson?.let {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val gson = Gson()
                    val nuevasRutas = gson.fromJson(it, Array<Rutas>::class.java).toList()

                    val sharedPrefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val lastKnownRutasJson = sharedPrefs.getString("last_known_rutas", null)
                    val lastKnownRutas = if (lastKnownRutasJson != null) {
                        gson.fromJson(lastKnownRutasJson, Array<Rutas>::class.java).toList()
                    } else {
                        emptyList()
                    }

                    if (nuevasRutas != lastKnownRutas) {
                        // Hay datos nuevos o modificados, guardar la nueva versión
                        sharedPrefs.edit().putString("last_known_rutas", gson.toJson(nuevasRutas)).apply()
                        Log.d("FCM_MESSAGE", "handleDataMessage: Datos cambiaron. Enviando notificación local.")
                        // Enviar notificación visible al usuario
                        withContext(Dispatchers.Main) {
                            sendNotification("Actualización de Rutas", "Hay nuevos datos de rutas disponibles.")
                            // También puedes enviar un broadcast para actualizar la UI si la actividad está abierta
                            val intent = Intent("com.example.segura_control.RUTAS_UPDATED")
                            applicationContext.sendBroadcast(intent)
                        }
                    } else {
                        Log.d("FCM_MESSAGE", "handleDataMessage: No hay cambios en los datos de rutas.")
                    }
                } catch (e: Exception) {
                    Log.e("FCM_MESSAGE", "Error procesando datos de rutas FCM: ${e.message}", e)
                }
            }
        }
    }

    /**
     * Crea y muestra una notificación push al usuario.
     * Este código es similar al que tenías en tu Worker.
     */
    private fun sendNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear un canal de notificación (necesario para Android 8.0 Oreo y superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones de Rutas FCM",
                NotificationManager.IMPORTANCE_DEFAULT // Puedes usar IMPORTANCE_HIGH para más visibilidad
            ).apply {
                description = "Canal para notificaciones de actualizaciones de rutas vía FCM"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir tu RutasActivity cuando se toca la notificación
        val intent = Intent(this, RutasActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // FLAG_IMMUTABLE es requerido para API 23+
        )

        // Construir la notificación
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // **¡IMPORTANTE!** Reemplaza con un icono de notificación válido
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Establecer el PendingIntent
            .setAutoCancel(true) // La notificación se cierra automáticamente al tocarla
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
        Log.d("FCM_MESSAGE", "Notificación FCM enviada.")
    }
}