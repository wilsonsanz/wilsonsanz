package com.example.calculadora_basica

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val numero1Input = findViewById<EditText>(R.id.numero1Input)
        val numero2Input = findViewById<EditText>(R.id.numero2Input)
        val operacionInput = findViewById<EditText>(R.id.operacionInput)
        val botonCalcular = findViewById<Button>(R.id.botonCalcular)
        val resultado = findViewById<TextView>(R.id.resultado)

        botonCalcular.setOnClickListener {
            try {
                val num1 = numero1Input.text.toString().toDouble()
                val num2 = numero2Input.text.toString().toDouble()
                val operacion = operacionInput.text.toString()

                // Realizar el cálculo según la operación
                val res = when (operacion) {
                    "+" -> num1 + num2
                    "-" -> num1 - num2
                    "*" -> num1 * num2
                    "/" -> {
                        if (num2 != 0.0) {
                            num1 / num2
                        } else {
                            "Error: División por cero"
                        }
                    }
                    else -> "Operación no válida"
                }

                // Mostrar el resultado
                resultado.text = "Resultado: $res"

            } catch (e: NumberFormatException) {
                resultado.text = "Por favor, ingresa números válidos."
            }
        }
    }
}
