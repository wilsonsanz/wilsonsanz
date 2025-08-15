package com.segura.Activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.segura_control.BaseUrl
import com.example.segura_control.Constantes
import com.example.segura_control.RetrofitClient
import com.example.segura_control.sheets.WikiData
import com.example.segura_control.databinding.WcActivityBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WikiActivity : AppCompatActivity() {

    private lateinit var binding: WcActivityBinding

    private val handler = Handler()

    private var startTimeMap = mutableMapOf<String, Long>()
    private var runnableMap = mutableMapOf<String, Runnable>()

    // Variable para almacenar el número de registro que se recibirá
    private var numeroMovilRecibido: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WcActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // **PASO 1: RECUPERAR EL NÚMERO DEL INTENT**
        numeroMovilRecibido = intent.getStringExtra("numeroRegistro")

        // **PASO 2: ASIGNAR EL NÚMERO AL EditText etMovil**
        if (!numeroMovilRecibido.isNullOrEmpty()) {
            binding.etMovil.setText(numeroMovilRecibido)
            // Opcional: Si quieres que el campo no sea editable después de establecer el número
            binding.etMovil.isEnabled = false
        } else {
            // Manejar el caso donde no se recibe el número (ej. si se abre esta Activity directamente)
            Toast.makeText(this, "Número de móvil no recibido.", Toast.LENGTH_SHORT).show()
        }


        // Configuración de cronómetros
        configurarCronometro("WC1", binding.tvCronoWC1)
        configurarCronometro("WC2", binding.tvCronoWC2)
        configurarCronometro("Desayuno", binding.tvCronoDesayuno)
        configurarCronometro("Almuerzo", binding.tvCronoAlmuerzo)
        configurarCronometro("Merienda", binding.tvCronoMerienda)

        // Botones START
        binding.btnStartWC1.setOnClickListener { iniciarCronometro("WC1") }
        binding.btnStartWC2.setOnClickListener { iniciarCronometro("WC2") }
        binding.btnStartDesayuno.setOnClickListener { iniciarCronometro("Desayuno") }
        binding.btnStartAlmuerzo.setOnClickListener { iniciarCronometro("Almuerzo") }
        binding.btnStartMerienda.setOnClickListener { iniciarCronometro("Merienda") }

        // Botones STOP
        binding.btnStopWC1.setOnClickListener { detenerCronometro("WC1") }
        binding.btnStopWC2.setOnClickListener { detenerCronometro("WC2") }
        binding.btnStopDesayuno.setOnClickListener { detenerCronometro("Desayuno") }
        binding.btnStopAlmuerzo.setOnClickListener { detenerCronometro("Almuerzo") }
        binding.btnStopMerienda.setOnClickListener { detenerCronometro("Merienda") }

        // Botón para enviar los datos
        binding.btnAgregar.setOnClickListener {
            agregarDataWiki()
        }
    }

    private fun configurarCronometro(nombre: String, textView: android.widget.TextView) {
        startTimeMap[nombre] = 0L // Inicializa el tiempo en 0
        val runnable = object : Runnable {
            override fun run() {
                val elapsed = SystemClock.elapsedRealtime() - (startTimeMap[nombre] ?: 0L)
                val minutes = (elapsed / 1000) / 60
                val seconds = (elapsed / 1000) % 60
                textView.text = String.format("%02d:%02d", minutes, seconds)
                handler.postDelayed(this, 1000)
            }
        }
        runnableMap[nombre] = runnable
    }

    private fun iniciarCronometro(nombre: String) {
        // Al iniciar, si ya hay un tiempo anterior, podemos decidir resetearlo o continuar.
        // Aquí lo reseteamos para que cada inicio sea desde 0.
        startTimeMap[nombre] = SystemClock.elapsedRealtime()
        // Asegúrate de que el TextView muestre "00:00" si se reinicia un cronómetro activo
        // Esto es útil si el usuario inicia y detiene varias veces.
        (runnableMap[nombre]?.let { handler.removeCallbacks(it) }) // Detiene cualquier cronómetro previo si está corriendo
        runnableMap[nombre]?.let { handler.post(it) }
    }

    private fun detenerCronometro(nombre: String) {
        runnableMap[nombre]?.let { handler.removeCallbacks(it) }
    }

    private fun agregarDataWiki() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val filas = mutableListOf<String>()

                // 1. Siempre se incluye el nombre del móvil como primer dato
                filas.add(binding.etMovil.text.toString().trim()) // Esto ahora contendrá el valor pasado

                // 2. Tiempos cronometrados en el orden especificado
                filas.add(binding.tvCronoWC1.text.toString())
                filas.add(binding.tvCronoWC2.text.toString())
                filas.add(binding.tvCronoDesayuno.text.toString())
                filas.add(binding.tvCronoAlmuerzo.text.toString())
                filas.add(binding.tvCronoMerienda.text.toString())

                // Opcional: Validar si al menos se ha introducido el móvil
                if (binding.etMovil.text.toString().trim().isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@WikiActivity, "Por favor, introduce el número de móvil.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }


                val wiki = WikiData(
                    action = "agregarWiki",
                    spreadsheet_id = Constantes.google_sheet_id,
                    sheet = Constantes.sheet5,
                    rows = listOf(filas)
                )

                val response = RetrofitClient.webService(BaseUrl.base_url_post).agregarWiki(wiki)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@WikiActivity, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()

                        // codigo para vibrar
                        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator.vibrate(200)
                        }

                        // Limpiar el campo del móvil (opcional, si quieres que se mantenga)
                        // binding.btnMovil.text?.clear() // Comentado si el campo debe mantener el número de móvil fijo

                        // Reiniciar todos los cronómetros después de un envío exitoso
                        resetearCronometros()

                        obtenerData()
                    } else {
                        Toast.makeText(this@WikiActivity, "Error al enviar: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@WikiActivity, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Función para resetear todos los cronómetros a "00:00"
    private fun resetearCronometros() {
        // Detiene cualquier hilo de actualización de tiempo que esté corriendo
        runnableMap.values.forEach { handler.removeCallbacks(it) }

        // Establece los TextViews de los cronómetros a "00:00"
        binding.tvCronoWC1.text = "00:00"
        binding.tvCronoWC2.text = "00:00"
        binding.tvCronoDesayuno.text = "00:00"
        binding.tvCronoAlmuerzo.text = "00:00"
        binding.tvCronoMerienda.text = "00:00"

        // Reinicia los tiempos de inicio en el mapa para que el próximo "start" comience desde cero.
        startTimeMap.keys.forEach { startTimeMap[it] = 0L }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Asegúrate de detener todos los runnables cuando la actividad se destruye para evitar fugas de memoria
        runnableMap.values.forEach { handler.removeCallbacks(it) }
    }

    private fun obtenerData() {
        // Aquí puedes implementar la lógica para actualizar datos si lo necesitas,
        // por ejemplo, recargar una lista en la UI con los datos más recientes de la hoja.
    }
}