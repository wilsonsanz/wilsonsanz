package com.example.sis_calificaciones

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Elementos de la interfaz
        val calificacionInput = findViewById<EditText>(R.id.calificacionInput)
        val botonCalificar = findViewById<Button>(R.id.botonCalificar)
        val mensajeCalificacion = findViewById<TextView>(R.id.mensajeCalificacion)

        // Acción al presionar el botón
        botonCalificar.setOnClickListener {
            // Obtener la calificación ingresada
            val calificacion = calificacionInput.text.toString().toIntOrNull()

            // Validar que la calificación sea un número válido
            if (calificacion != null) {
                // Calcular la letra correspondiente y mostrar el resultado
                val letraCalificacion = obtenerLetraCalificacion(calificacion)
                mensajeCalificacion.text = "Tu calificación es $letraCalificacion"
            } else {
                // Mostrar un mensaje de error si la entrada no es válida
                Toast.makeText(this, "Por favor, ingresa una calificación válida", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función que obtiene la letra correspondiente a la calificación
    fun obtenerLetraCalificacion(calificacion: Int): String {
        return when {
            calificacion in 90..100 -> "A"
            calificacion in 80..89 -> "B"
            calificacion in 70..79 -> "C"
            calificacion in 60..69 -> "D"
            else -> "F"
        }
    }
}

