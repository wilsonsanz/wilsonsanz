package com.example.multiplicar_pares_conteoregre

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializamos los elementos de la interfaz
        val numberInput: EditText = findViewById(R.id.numberInput)
        val resultText: TextView = findViewById(R.id.resultText)

        // Botón para mostrar la tabla de multiplicar
        val multiplyButton: Button = findViewById(R.id.multiplyButton)
        multiplyButton.setOnClickListener {
            val number = numberInput.text.toString().toIntOrNull()
            if (number != null) {
                val table = StringBuilder()
                for (i in 1..10) {
                    table.append("$number x $i = ${number * i}\n")
                }
                resultText.text = table.toString()
            } else {
                resultText.text = "Por favor, introduce un número válido."
            }
        }

        // Botón para contar los números pares
        val evenNumbersButton: Button = findViewById(R.id.evenNumbersButton)
        evenNumbersButton.setOnClickListener {
            val evens = StringBuilder()
            for (i in 1..20) {
                if (i % 2 == 0) {
                    evens.append("$i ")
                }
            }
            resultText.text = evens.toString()
        }

        // Botón para el contador regresivo
        val countdownButton: Button = findViewById(R.id.countdownButton)
        countdownButton.setOnClickListener {
            val countdown = StringBuilder()
            for (i in 10 downTo 1) {
                countdown.append("$i ")
            }
            resultText.text = countdown.toString()
        }
    }
}
