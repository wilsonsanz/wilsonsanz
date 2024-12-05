package com.example.juegonumero

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Elementos de la interfaz
        val numeroInput = findViewById<EditText>(R.id.numeroInput)
        val botonAdivinar = findViewById<Button>(R.id.botonAdivinar)
        val mensajeResultado = findViewById<TextView>(R.id.mensajeResultado)

        // Generar un número aleatorio entre 1 y 10
        val numeroAleatorio = Random.nextInt(1, 11)

        // Acción al presionar el botón
        botonAdivinar.setOnClickListener {
            val numeroIngresado = numeroInput.text.toString().toIntOrNull()

            // Validar si el número ingresado es válido
            if (numeroIngresado != null) {
                if (numeroIngresado == numeroAleatorio) {
                    mensajeResultado.text = "¡Felicidades, acertaste!"
                } else {
                    mensajeResultado.text = "Intenta de nuevo."
                }
            } else {
                // Mostrar mensaje de error si el número ingresado no es válido
                Toast.makeText(this, "Por favor, ingresa un número válido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
