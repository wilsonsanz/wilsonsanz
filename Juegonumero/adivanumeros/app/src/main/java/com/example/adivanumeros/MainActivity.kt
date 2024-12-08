package com.example.adivanumeros

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var randomNumber = 0
    private var attemptsLeft = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputNumber: EditText = findViewById(R.id.inputNumber)
        val btnAdivi: Button = findViewById(R.id.btnAdivi)
        val feedbackText: TextView = findViewById(R.id.feedbackText)
        val attemptsLeftText: TextView = findViewById(R.id.attemptsLeft)
        val btnRestart: Button = findViewById(R.id.btnRestart)

        // Inicializar juego
        startNewGame()

        // Lógica al presionar el botón Adivinar
        btnAdivi.setOnClickListener {
            val userInput = inputNumber.text.toString()

            if (userInput.isEmpty()) {
                feedbackText.text = "Por favor, ingresa un número."
                return@setOnClickListener
            }

            val guessedNumber = userInput.toInt()

            when {
                guessedNumber == randomNumber -> {
                    feedbackText.text = "¡Correcto! Has adivinado el número."
                    btnAdivi.visibility = View.GONE
                    btnRestart.visibility = View.VISIBLE
                }
                guessedNumber > randomNumber -> {
                    feedbackText.text = "Demasiado alto. Intenta con un número más bajo."
                }
                else -> {
                    feedbackText.text = "Demasiado bajo. Intenta con un número más alto."
                }
            }

            // Reducir intentos
            attemptsLeft--
            attemptsLeftText.text = "Intentos restantes: $attemptsLeft"

            if (attemptsLeft == 0) {
                feedbackText.text = "Has perdido. El número correcto era $randomNumber."
                btnAdivi.visibility = View.GONE
                btnRestart.visibility = View.VISIBLE
            }

            inputNumber.text.clear()
        }

        // Lógica para reiniciar el juego
        btnRestart.setOnClickListener {
            startNewGame()
            feedbackText.text = "Ingresa un número para empezar"
            attemptsLeftText.text = "Intentos restantes: $attemptsLeft"
            btnAdivi.visibility = View.VISIBLE
            btnRestart.visibility = View.GONE
        }
    }

    private fun startNewGame() {
        randomNumber = Random.nextInt(1, 101) // Genera un número entre 1 y 100
        attemptsLeft = 10
    }
}
