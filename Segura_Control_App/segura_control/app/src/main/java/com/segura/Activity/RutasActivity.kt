package com.segura.Activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper // Importar Looper
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.segura_control.BaseUrl
import com.example.segura_control.Constantes
import com.example.segura_control.OnRutaDataChangedListener
import com.example.segura_control.R
import com.example.segura_control.RetrofitClient
import com.example.segura_control.sheets.Rutas
import com.example.segura_control.RutasAdapter
import com.example.segura_control.RutasUpdateWorker
import com.example.segura_control.databinding.RutasActivityBinding
import com.example.segura_control.sheets.datosData
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class RutasActivity : AppCompatActivity(), OnRutaDataChangedListener {

    private lateinit var binding: RutasActivityBinding
    private lateinit var adapter: RutasAdapter

    private var listaCompletaRutas: MutableList<Rutas> = mutableListOf()
    private var numeroMovilPredeterminado: String? = null

    private lateinit var vibrator: Vibrator

    // --- Variables para la actualización automática ---
    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 30000L // 30 segundos en milisegundos

    private val refreshRunnable = object : Runnable {
        override fun run() {
            Log.d("RutasActivity", "Ejecutando refreshRunnable: Obteniendo datos.")
            obtenerDataRutas() // Llama a tu función para obtener los datos actualizados
            handler.postDelayed(this, refreshInterval) // Vuelve a programar la ejecución
        }
    }
    // --- Fin de variables para la actualización automática ---




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RutasActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // **AQUÍ ES DONDE DEBE IR EL CÓDIGO PARA OBTENER EL TOKEN DE FCM**
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_TOKEN", "Fallo al obtener token de registro", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("FCM_TOKEN", "Token de registro: $token")
            // Aquí puedes enviar el token a tu servidor si lo necesitas
            // sendTokenToServer(token)
        }

        numeroMovilPredeterminado = intent.getStringExtra("numeroMovil")
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        setupRecyclerView()
        scheduleRutasUpdateWork() // Comentado: Ya no usamos WorkManager para el refresco rápido
    }

    override fun onResume() {
        super.onResume()
        Log.d("RutasActivity", "onResume: Iniciando refresco automático.")
        startAutoRefresh() // <-- ¡Asegúrate de que esto esté activo!
    }



    override fun onPause() {
        super.onPause()
        Log.d("RutasActivity", "onPause: Deteniendo refresco automático y guardando rutas.")
        stopAutoRefresh() // Detiene el refresco automático cuando la actividad se pausa
        saveRutasToSharedPreferences(listaCompletaRutas)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAutoRefresh() // Asegúrate de detenerlo completamente para evitar fugas de memoria
    }


    private fun setupRecyclerView() {
        adapter = RutasAdapter(emptyList(), this)
        binding.rvRutas.layoutManager = LinearLayoutManager(this)
        binding.rvRutas.adapter = adapter
    }

    override fun onRutaDataSent(position: Int, updatedRuta: Rutas) {
        if (position != RecyclerView.NO_POSITION && position < listaCompletaRutas.size) {
            listaCompletaRutas[position] = updatedRuta
        }
        enviarDatosRuta(updatedRuta)

    }

    private fun enviarDatosRuta(ruta: Rutas) {
        val numeroMovil = ruta.MOVIL?.trim() ?: ""
        val telefono = ruta.TELEFONO?.trim() ?: ""
        val copiloto1 = ruta.COPILOTO?.trim() ?: ""
        val copiloto2 = ruta.COPILOTO_2?.trim() ?: ""
        val copiloto3 = ruta.COPILOTO_3?.trim() ?: ""

        if (numeroMovil.isEmpty() || telefono.isEmpty() || copiloto1.isEmpty()) {
            Toast.makeText(this, "Por favor, complete al menos el Número de Móvil, Teléfono y Copiloto 1.", Toast.LENGTH_SHORT).show()
            return
        }

        val rowDataForDatosSheet = listOf(
            numeroMovil,
            telefono,
            copiloto1,
            copiloto2,
            copiloto3
        )

        val datosParaHojaDatos = datosData(
            action = "escribirDatosEnA2E2",
            spreadsheet_id = Constantes.google_sheet_id,
            sheet = Constantes.sheet7,
            rows = listOf(rowDataForDatosSheet)
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService(BaseUrl.base_url_post2).agregarDatos(datosParaHojaDatos)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Toast.makeText(this@RutasActivity, "Datos enviados a hoja 'datos': ${responseBody?.rows ?: "Éxito"}", Toast.LENGTH_LONG).show()
                        vibrator.vibrate(100)

                        // Una vez que se envían datos, es buena idea actualizar la UI
                        obtenerDataRutas() // Sí, mantenemos esto para una actualización inmediata tras el envío.
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@RutasActivity, "Error al enviar a hoja 'datos': ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RutasActivity, "Excepción al enviar datos a hoja 'datos': ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }

    private fun obtenerDataRutas() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.webService(BaseUrl.base_url_get).obtenerTodoRutas()
                if (response.isSuccessful) {
                    val nuevasRutas = response.body()?.rutas ?: emptyList()

                    // **Lógica de comparación de datos para notificaciones:**
                    val sharedPrefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val lastKnownRutasJson = sharedPrefs.getString("last_known_rutas", null)

                    val gson = Gson()
                    val lastKnownRutas = if (lastKnownRutasJson != null) {
                        gson.fromJson(lastKnownRutasJson, Array<Rutas>::class.java).toList()
                    } else {
                        emptyList()
                    }

                    if (nuevasRutas != lastKnownRutas) {
                        Log.d("RutasActivity", "obtenerDataRutas: ¡Datos cambiaron! Actualizando UI y enviando notificación.")
                        // Guardar la nueva versión para futuras comparaciones
                        saveRutasToSharedPreferences(nuevasRutas)

                        withContext(Dispatchers.Main) {
                            listaCompletaRutas = nuevasRutas.toMutableList()
                            adapter.actualizarLista(listaCompletaRutas)
                            setupSpinner()
                            // **¡Enviar notificación aquí!**
                            sendNotification("Actualización de Rutas", "Hay nuevos datos de rutas disponibles.")
                        }
                    } else {
                        Log.d("RutasActivity", "obtenerDataRutas: No hay cambios en los datos.")
                        withContext(Dispatchers.Main) {
                            // Aunque no haya cambios, podrías querer actualizar la UI con los datos actuales
                            // para asegurar que siempre está sincronizada, o solo si el usuario no ha interactuado.
                            // Por ahora, solo actualizamos si hay cambios significativos para la notificación.
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RutasActivity, "Error al obtener datos: ${response.message()}", Toast.LENGTH_LONG).show()
                        Log.e("RutasActivity", "Error al obtener datos: ${response.code()} - ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RutasActivity, "Excepción al obtener datos: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("RutasActivity", "Excepción al obtener datos: ${e.message}", e)
                }
            }
        }
    }

    private fun setupSpinner() {
        // 1. Guardar la selección actual antes de recrear el adaptador
        val currentSelectedMovil = binding.spinnerMoviles.selectedItem?.toString()

        val numerosMoviles = listaCompletaRutas
            .mapNotNull { it.MOVIL }
            .distinct()
            .sorted()

        val spinnerItems = mutableListOf("Ver Todos")
        spinnerItems.addAll(numerosMoviles)

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            spinnerItems
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMoviles.adapter = spinnerAdapter

        // 2. Restaurar la selección
        // Intenta seleccionar el elemento que estaba previamente seleccionado
        if (currentSelectedMovil != null && spinnerItems.contains(currentSelectedMovil)) {
            val index = spinnerItems.indexOf(currentSelectedMovil)
            if (index != -1) {
                binding.spinnerMoviles.setSelection(index)
            }
        } else {
            // Si no hay una selección previa o el elemento ya no existe,
            // intenta seleccionar el número de móvil predeterminado (si lo hay)
            numeroMovilPredeterminado?.let { numero ->
                val index = spinnerItems.indexOf(numero)
                if (index != -1) {
                    binding.spinnerMoviles.setSelection(index)
                }
            }
        }

        binding.spinnerMoviles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMovil = parent?.getItemAtPosition(position).toString()
                if (selectedMovil == "Ver Todos") {
                    adapter.actualizarLista(listaCompletaRutas)
                } else {
                    val listaFiltrada = listaCompletaRutas.filter { it.MOVIL == selectedMovil }
                    adapter.actualizarLista(listaFiltrada)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed
            }
        }
    }
    // Asegúrate de tener esta función en RutasActivity
    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "rutas_activity_channel", // Usa un ID de canal diferente al del Worker/FCM si quieres reglas de notificación distintas
                "Notificaciones de Rutas (App Activa)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para notificaciones de actualizaciones de rutas (cuando la app está activa)"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, RutasActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "rutas_activity_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(103, notification) // Usa un ID de notificación diferente si es de la actividad
        Log.d("RutasActivity", "Notificación enviada desde la actividad.")
    }

    // --- Nuevos métodos para controlar el Handler ---
    private fun startAutoRefresh() {
        handler.post(refreshRunnable) // Inicia la primera ejecución inmediatamente
    }

    private fun stopAutoRefresh() {
        handler.removeCallbacks(refreshRunnable) // Detiene las ejecuciones pendientes
    }
    // --- Fin de nuevos métodos ---

    // Comentado: Ya no se usa WorkManager para el refresco rápido
    private fun scheduleRutasUpdateWork() {
        val updateWorkRequest = PeriodicWorkRequestBuilder<RutasUpdateWorker>(
            1, TimeUnit.MINUTES
        )
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "RutasUpdaterWork",
            ExistingPeriodicWorkPolicy.UPDATE,
            updateWorkRequest
        )
        Toast.makeText(this, "Actualización periódica programada (Worker)", Toast.LENGTH_SHORT).show()
    }

    private fun saveRutasToSharedPreferences(rutas: List<Rutas>) {
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val gson = Gson()
        val json = gson.toJson(rutas)
        editor.putString("last_known_rutas", json)
        editor.apply()
        Log.d("RutasActivity", "Rutas guardadas en SharedPreferences.")
    }

}