package com.example.segura_control.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.segura_control.MainActivity // Tu MainActivity para abrirla desde la notificación
import com.example.segura_control.R // Asegúrate de que R exista y tenga íconos, etc.
import com.example.segura_control.models.LocationData // Tu modelo de datos de ubicación
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class LocationTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var db: FirebaseFirestore

    // Constantes para la notificación del Foreground Service
    companion object {
        const val CHANNEL_ID = "LocationServiceChannel"
        const val NOTIFICATION_ID = 123
        const val ACTION_STOP_SERVICE = "STOP_SERVICE"
        const val ACTION_START_SERVICE = "START_SERVICE" // Añadido para mejor control
    }

    // Variables para almacenar la información del conductor
    private var nombreConductor: String? = null
    private var numeroMovil: String? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("LocationService", "Servicio creado.")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        db = FirebaseFirestore.getInstance() // Inicializa Firestore

        // Inicializar LocationCallback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0.lastLocation?.let { location ->
                    Log.d("LocationService", "Ubicación recibida: Lat ${location.latitude}, Lon ${location.longitude}")
                    // Aquí llamamos a la función para guardar en Firestore
                    saveLocationToFirestore(location)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Extrae la información del conductor del Intent
        nombreConductor = intent?.getStringExtra("nombreConductor")
        numeroMovil = intent?.getStringExtra("numeroMovil")
        Log.d("LocationService", "Servicio iniciado. Conductor: $nombreConductor, Móvil: $numeroMovil")

        // Crear canal de notificación (necesario para Android O y superior)
        createNotificationChannel()

        // Crear la notificación persistente
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Seguimiento de Ubicación")
            .setContentText("Tu ubicación está siendo compartida.")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener un icono aquí
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW) // Prioridad baja para que no sea intrusivo
            .build()

        startForeground(NOTIFICATION_ID, notification) // Iniciar el servicio en primer plano

        // Iniciar las actualizaciones de ubicación
        startLocationUpdates()

        // Si el servicio es "matado" por el sistema, reiniciarlo
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // No necesitamos un binder para este servicio simple
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates() // Detener actualizaciones de ubicación al destruir el servicio
        Log.d("LocationService", "Servicio destruido y seguimiento detenido.")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Canal de Servicio de Ubicación",
                NotificationManager.IMPORTANCE_LOW // Importancia baja para la notificación persistente
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                30000L // Intervalo de actualización en milisegundos (30 segundos)
            )
                .setMinUpdateIntervalMillis(15000L) // Intervalo mínimo de actualización (opcional)
                .setMaxUpdateDelayMillis(60000L) // Retraso máximo de actualización (opcional)
                .build()

            // Usar Looper.getMainLooper() para el callback si no hay un hilo específico
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            Log.d("LocationService", "Solicitando actualizaciones de ubicación...")
        } else {
            Log.e("LocationService", "Permisos de ubicación no concedidos. No se pudo iniciar el seguimiento.")
            // En un servicio, no puedes mostrar un Toast o pedir permisos directamente.
            // Esto debería manejarse en la Activity antes de iniciar el servicio.
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("LocationService", "Actualizaciones de ubicación detenidas.")
    }

    // --- Función para guardar la ubicación en Firestore ---
    private fun saveLocationToFirestore(location: Location) {
        val conductorId = numeroMovil ?: "desconocido" // Usa el número de móvil como ID único para el conductor
        if (conductorId == "desconocido") {
            Log.e("Firestore", "No se pudo obtener el numeroMovil para el ID del conductor. Saltando el guardado en Firestore.")
            return
        }

        val locationData = LocationData(
            latitud = location.latitude,
            longitud = location.longitude,
            conductor = nombreConductor,
            movil = numeroMovil,
            precision = location.accuracy
            // timestamp se añadirá automáticamente por @ServerTimestamp
        )

        // Guarda la última ubicación del conductor
        db.collection("ubicaciones_actuales")
            .document(conductorId)
            .set(locationData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "Última ubicación de $conductorId actualizada correctamente.")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al actualizar última ubicación de $conductorId", e)
            }

        // Guarda el historial de ubicaciones
        db.collection("conductores")
            .document(conductorId)
            .collection("historial_ubicaciones")
            .add(locationData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Ubicación de $conductorId añadida al historial con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al añadir ubicación al historial para $conductorId", e)
            }
    }
}