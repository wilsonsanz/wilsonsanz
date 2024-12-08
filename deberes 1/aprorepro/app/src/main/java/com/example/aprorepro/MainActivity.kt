package com.example.aprorepro

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var calificacionEstudiante: EditText
    private lateinit var calcular: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa las vistas
        calificacionEstudiante = findViewById(R.id.calificacionEstudiante)
        calcular = findViewById(R.id.btnCalcular)

        // Configura el botón para calcular si la calificación es aprobatoria
        calcular.setOnClickListener {
            verificarAprobacion()
        }
    }

    private fun verificarAprobacion() {
        // Obtiene el texto ingresado y lo convierte a un número
        val calificacionTexto = calificacionEstudiante.text.toString()
        if (calificacionTexto.isNotEmpty()) {
            val calificacion = calificacionTexto.toDoubleOrNull()
            if (calificacion != null) {
                // Determina si la calificación es aprobatoria o reprobatoria
                val resultado = if (calificacion >= 7) {
                    "Aprobado"
                } else {
                    "Reprobado"
                }
                // Muestra el resultado en un Toast
                Toast.makeText(this, "Resultado: $resultado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingresa una calificación válida.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "El campo no puede estar vacío.", Toast.LENGTH_SHORT).show()
        }
    }
}
