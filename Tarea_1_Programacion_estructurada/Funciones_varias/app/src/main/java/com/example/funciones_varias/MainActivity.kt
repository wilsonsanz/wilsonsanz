package com.example.funciones_varias

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los elementos de la UI
        val inputNombre: EditText = findViewById(R.id.inputNombre)
        val inputNumero1: EditText = findViewById(R.id.inputNumero1)
        val outputText: TextView = findViewById(R.id.outputText)

        // Función de saludo personalizado
        val btnSaludo: Button = findViewById(R.id.btnSaludo)
        btnSaludo.setOnClickListener {
            val nombre = inputNombre.text.toString()
            outputText.text = saludoPersonalizado(nombre)
        }

        // Suma de dos números
        val btnSuma: Button = findViewById(R.id.btnSuma)
        btnSuma.setOnClickListener {
            val num1 = inputNumero1.text.toString().toInt()
            val num2 = inputNombre.text.toString().toInt()
            val resultado = sumaDeDosNumeros(num1, num2)
            outputText.text = "Resultado: $resultado"
        }

        // Verificar si el número es par o impar
        val btnParImpar: Button = findViewById(R.id.btnParImpar)
        btnParImpar.setOnClickListener {
            val num = inputNumero1.text.toString().toInt()
            val esPar = esPar(num)
            outputText.text = "Es par: $esPar"
        }

        // Calcular el cuadrado de un número
        val btnCuadrado: Button = findViewById(R.id.btnCuadrado)
        btnCuadrado.setOnClickListener {
            val num = inputNumero1.text.toString().toInt()
            val cuadrado = calcularCuadrado(num)
            outputText.text = "Cuadrado: $cuadrado"
        }

        // Calcular el área del círculo
        val btnAreaCirculo: Button = findViewById(R.id.btnAreaCirculo)
        btnAreaCirculo.setOnClickListener {
            val radio = inputNumero1.text.toString().toDouble()
            val area = calcularAreaCirculo(radio)
            outputText.text = "Área del círculo: $area"
        }
    }

    // Funciones

    fun saludoPersonalizado(nombre: String): String {
        return "Hola, $nombre!"
    }

    fun sumaDeDosNumeros(num1: Int, num2: Int): Int {
        return num1 + num2
    }

    fun esPar(numero: Int): Boolean {
        return numero % 2 == 0
    }

    fun calcularCuadrado(numero: Int): Int {
        return numero * numero
    }

    fun calcularAreaCirculo(radio: Double): Double {
        val pi = 3.14159
        return pi * radio * radio
    }
}
