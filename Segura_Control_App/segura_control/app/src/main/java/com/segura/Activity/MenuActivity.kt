package com.segura.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View // Importa la clase View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.segura_control.MainActivity
import com.example.segura_control.R
import com.example.segura_control.RutasUpdateWorker
import java.util.concurrent.TimeUnit

class MenuActivity : AppCompatActivity() {

    private lateinit var tvNumeroRegistro: TextView
    private lateinit var etNumeroIngresado: EditText
    private lateinit var btnEnviarMovil: Button
    private lateinit var btnSalir: Button

    private var numeroRegistroActual: String = "No disponible"
    private var nombreConductorGuardado: String? = null
    private var numeroMovil : String? = null


    private fun scheduleRutasUpdateWork() {
        val updateWorkRequest = OneTimeWorkRequestBuilder<RutasUpdateWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS) // Primer retraso
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "RutasUpdaterWork",
            ExistingWorkPolicy.REPLACE, // Reemplazar si ya existe
            updateWorkRequest
        )
        Toast.makeText(this, "Actualización programada (Worker)", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val tvWelcomeMessage = findViewById<TextView>(R.id.tvWelcomeMessage)
        tvNumeroRegistro = findViewById(R.id.tvNumeroRegistro)
        etNumeroIngresado = findViewById(R.id.etNumeroIngresado)
        btnEnviarMovil = findViewById(R.id.btnEnviarMovil)
        btnSalir = findViewById(R.id.btnSalir)

        // Recupera el nombre del conductor y el número de móvil del Intent
        val nombreConductorRecibidoPorIntent = intent.getStringExtra("nombreConductor")
        val numeroMovilRecibidoPorIntent = intent.getStringExtra("numeroMovil")

        if (!nombreConductorRecibidoPorIntent.isNullOrEmpty()) {
            nombreConductorGuardado = nombreConductorRecibidoPorIntent
            tvWelcomeMessage.text = "Bienvenido Sr. $nombreConductorGuardado"
        } else {
            tvWelcomeMessage.text = "Bienvenido a la aplicación"
            nombreConductorGuardado = "Invitado"
        }

        // --- Lógica para el número de móvil ---
        if (!numeroMovilRecibidoPorIntent.isNullOrEmpty()) {
            numeroMovil = numeroMovilRecibidoPorIntent // Asigna el valor a la variable de clase
            numeroRegistroActual = numeroMovil!! // Usa !! porque ya verificamos que no es nulo/vacío
            tvNumeroRegistro.text = "Móvil registrado: $numeroRegistroActual"
            etNumeroIngresado.setText(numeroRegistroActual) // Pre-llenar el EditText

            // *** ¡CAMBIO CLAVE AQUÍ! HACERLOS DESAPARECER ***
            etNumeroIngresado.visibility = View.GONE
            btnEnviarMovil.visibility = View.GONE

        } else {
            // Si numeroMovil está vacío o nulo, permitir ingreso manual
            tvNumeroRegistro.text = "Móvil no establecido" // Mensaje inicial

            // *** ¡CAMBIO CLAVE AQUÍ! ASEGURAR QUE SEAN VISIBLES SI SE NECESITA EL INGRESO MANUAL ***
            etNumeroIngresado.visibility = View.VISIBLE
            btnEnviarMovil.visibility = View.VISIBLE

            Toast.makeText(this, "Por favor, ingrese su número de móvil.", Toast.LENGTH_LONG).show()
        }

        // Lógica del botón "ENVIAR MÓVIL" (ahora solo se usará si el móvil no vino del login)
        btnEnviarMovil.setOnClickListener {
            val inputNumero = etNumeroIngresado.text.toString().trim()
            if (inputNumero.isNotEmpty()) {
                numeroRegistroActual = inputNumero
                numeroMovil = inputNumero // También actualiza la variable de clase numeroMovil
                tvNumeroRegistro.text = "Móvil registrado: $numeroRegistroActual"
                Toast.makeText(this, "Móvil registrado: $numeroRegistroActual", Toast.LENGTH_SHORT).show()

                // *** ¡CAMBIO CLAVE AQUÍ! HACERLOS DESAPARECER UNA VEZ INGRESADO MANUALMENTE ***
                etNumeroIngresado.visibility = View.GONE
                btnEnviarMovil.visibility = View.GONE

            } else {
                Toast.makeText(this, "Por favor, ingrese un número de móvil.", Toast.LENGTH_SHORT).show()
            }
        }

        val btncita = findViewById<Button>(R.id.btncita)
        val btningreso = findViewById<Button>(R.id.btningreso)
        val btnhojaderuta = findViewById<Button>(R.id.bthojaderutas)
        val btnTiempospersonales = findViewById<Button>(R.id.btnTiempospersonales)


        // Se mantiene la lógica de verificar numeroRegistroActual antes de pasar a otras actividades
        btncita.setOnClickListener {
            if (numeroRegistroActual == "No disponible" || numeroRegistroActual.isEmpty()) {
                Toast.makeText(this, "Primero debe enviar el número de móvil.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, novedades_activity::class.java)
                intent.putExtra("numeroRegistro", numeroRegistroActual)
                intent.putExtra("nombreConductor", nombreConductorGuardado)
                startActivity(intent)
            }
        }
        btningreso.setOnClickListener {
            if (numeroRegistroActual == "No disponible" || numeroRegistroActual.isEmpty()) {
                Toast.makeText(this, "Primero debe enviar el número de móvil.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, Ingreso_Salida_Activity::class.java)
                intent.putExtra("numeroRegistro", numeroRegistroActual)
                intent.putExtra("nombreConductor", nombreConductorGuardado)
                startActivity(intent)
            }
        }
        btnhojaderuta.setOnClickListener {
            if (numeroRegistroActual == "No disponible" || numeroRegistroActual.isEmpty()) {
                Toast.makeText(this, "Primero debe enviar el número de móvil.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, RutasActivity::class.java)
                intent.putExtra("numeroRegistro", numeroRegistroActual)
                intent.putExtra("nombreConductor", nombreConductorGuardado)
                startActivity(intent)
            }
        }
        btnTiempospersonales.setOnClickListener {
            if (numeroRegistroActual == "No disponible" || numeroRegistroActual.isEmpty()) {
                Toast.makeText(this, "Primero debe enviar el número de móvil.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, WikiActivity::class.java)
                intent.putExtra("numeroRegistro", numeroRegistroActual)
                intent.putExtra("nombreConductor", nombreConductorGuardado)
                startActivity(intent)
            }
        }

        // --- Lógica para el botón Salir ---
        btnSalir.setOnClickListener {
            val sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            editor.putBoolean("isLoggedIn", false)
            editor.remove("nombreConductor")
            editor.remove("numeroMovil")
            editor.apply()

            Toast.makeText(this, "Sesión cerrada.", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}