package com.example.ao_bisiesto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val annoInput = findViewById<EditText>(R.id.annoInput)
        val botonVerificar = findViewById<Button>(R.id.botonVerificar)
        val resultado = findViewById<TextView>(R.id.resultado)

        botonVerificar.setOnClickListener {
            try {
                val anno = annoInput.text.toString().toInt()

                // Verificar si es año bisiesto
                val esBisiesto = if ((anno % 4 == 0 && anno % 100 != 0) || (anno % 400 == 0)) {
                    "Es bisiesto"
                } else {
                    "No es bisiesto"
                }

                // Mostrar el resultado
                resultado.text = esBisiesto

            } catch (e: NumberFormatException) {
                resultado.text = "Por favor, ingresa un año válido."
            }
        }
    }
}
