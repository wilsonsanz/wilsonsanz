package com.example.edadparavotar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.edadparavotar.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los elementos de la interfaz
        val etEdad: EditText = findViewById(R.id.etEdad)
        val btnVerificar: Button = findViewById(R.id.btnVerificar)
        val tvResultado: TextView = findViewById(R.id.tvResultado)

        // Configuración del botón
        btnVerificar.setOnClickListener {
            val edadTexto = etEdad.text.toString()

            if (edadTexto.isNotEmpty()) {
                val edad = edadTexto.toInt()
                val resultado = if (edad >= 18) {
                    "Puedes votar"
                } else {
                    "No puedes votar"
                }
                tvResultado.text = resultado
            } else {
                tvResultado.text = "Por favor, ingresa una edad válida."
            }
        }
    }
}
