package com.example.ejerci_interm

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val numberInput: EditText = findViewById(R.id.numberInput)
        val resultText: TextView = findViewById(R.id.resultText)

        // Botón para calcular el factorial
        val factorialButton: Button = findViewById(R.id.factorialButton)
        factorialButton.setOnClickListener {
            val number = numberInput.text.toString().toIntOrNull()
            if (number != null && number >= 0) {
                resultText.text = "El factorial de $number es: ${factorial(number)}"
            } else {
                resultText.text = "Por favor, introduce un número válido."
            }
        }

        // Botón para mostrar los números primos entre 1 y 50
        val primeNumbersButton: Button = findViewById(R.id.primeNumbersButton)
        primeNumbersButton.setOnClickListener {
            resultText.text = "Números primos: ${primeNumbers()}"
        }

        // Botón para sumar los dígitos de un número
        val sumDigitsButton: Button = findViewById(R.id.sumDigitsButton)
        sumDigitsButton.setOnClickListener {
            val number = numberInput.text.toString().toIntOrNull()
            if (number != null) {
                resultText.text = "La suma de los dígitos de $number es: ${sumDigits(number)}"
            } else {
                resultText.text = "Por favor, introduce un número válido."
            }
        }

        // Botón para invertir un número
        val invertNumberButton: Button = findViewById(R.id.invertNumberButton)
        invertNumberButton.setOnClickListener {
            val number = numberInput.text.toString().toIntOrNull()
            if (number != null) {
                resultText.text = "El número invertido de $number es: ${invertNumber(number)}"
            } else {
                resultText.text = "Por favor, introduce un número válido."
            }
        }

        }
    }

    // Función para calcular el factorial
    private fun factorial(n: Int): Int {
        return if (n == 0) 1 else n * factorial(n - 1)
    }

    // Función para encontrar los números primos entre 1 y 50
    private fun primeNumbers(): String {
        val primes = mutableListOf<Int>()
        for (i in 2..50) {
            if (isPrime(i)) primes.add(i)
        }
        return primes.joinToString(", ")
    }

    // Función para verificar si un número es primo
    private fun isPrime(n: Int): Boolean {
        for (i in 2 until n) {
            if (n % i == 0) return false
        }
        return n > 1
    }

    // Función para sumar los dígitos de un número
    private fun sumDigits(number: Int): Int {
        var sum = 0
        var num = number
        while (num != 0) {
            sum += num % 10
            num /= 10
        }
        return sum
    }

    // Función para invertir un número
    private fun invertNumber(number: Int): Int {
        var num = number
        var reversed = 0
        while (num != 0) {
            val digit = num % 10
            reversed = reversed * 10 + digit
            num /= 10
        }
        return reversed
    }

