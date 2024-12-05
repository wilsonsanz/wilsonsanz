package com.example.numeroposionega

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.numeroposionega.R

class MainActivity : AppCompatActivity() {

    private lateinit var numeroUsuario: EditText
    private lateinit var calcular: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa las vistas
        numeroUsuario = findViewById(R.id.numeroUsuario)
        calcular = findViewById(R.id.btnCalcular)

        // Configura el botón para analizar el número
        calcular.setOnClickListener {
            analizarNumero()
        }
    }

    private fun analizarNumero() {
        // Obtiene el texto ingresado y lo convierte en número
        val numeroTexto = numeroUsuario.text.toString()
        if (numeroTexto.isNotEmpty()) {
            val numero = numeroTexto.toIntOrNull()
            if (numero != null) {
                // Determina si es positivo, negativo o cero
                val resultado = when {
                    numero > 0 -> "Es un número positivo."
                    numero < 0 -> "Es un número negativo."
                    else -> "Es cero."
                }
                // Muestra el resultado en un Toast
                Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingresa un número válido.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "El campo no puede estar vacío.", Toast.LENGTH_SHORT).show()
        }
    }
}
