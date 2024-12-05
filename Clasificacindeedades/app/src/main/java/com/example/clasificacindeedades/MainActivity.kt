package com.example.clasificacindeedades


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edadInput = findViewById<EditText>(R.id.edadInput)
        val botonClasificar = findViewById<Button>(R.id.botonClasificar)
        val resultado = findViewById<TextView>(R.id.resultado)

        botonClasificar.setOnClickListener {
            try {
                val edad = edadInput.text.toString().toInt()

                // Clasificación de la edad
                val clasificacion = when {
                    edad in 0..12 -> "Eres niño"
                    edad in 13..17 -> "Eres adolescente"
                    edad >= 18 -> "Eres adulto"
                    else -> "Edad no válida"
                }

                // Mostrar resultado
                resultado.text = clasificacion
            } catch (e: NumberFormatException) {
                // Manejo de errores si no se ingresa un número válido
                resultado.text = "Por favor, ingresa una edad válida."
            }
        }
    }
}
