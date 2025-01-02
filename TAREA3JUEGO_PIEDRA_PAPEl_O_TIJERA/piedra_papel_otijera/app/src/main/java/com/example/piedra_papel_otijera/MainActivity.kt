package com.example.piedra_papel_otijera

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.piedra_papel_otijera.logic.Player
import com.example.piedra_papel_otijera.models.GameLogic

class MainActivity : AppCompatActivity() {

    private val player = Player("Jugador")
    private val computer = Player("Computadora")
    private val gameLogic = GameLogic()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los elementos de la UI
        val btnRock: Button = findViewById(R.id.btnRock)
        val btnPaper: Button = findViewById(R.id.btnPaper)
        val btnScissors: Button = findViewById(R.id.btnScissors)
        val tvResult: TextView = findViewById(R.id.tvResult)
        val tvPlayerScore: TextView = findViewById(R.id.tvPlayerScore)
        val tvComputerScore: TextView = findViewById(R.id.tvComputerScore)
        val tvComputerChoice: TextView = findViewById(R.id.tvComputerChoice)
        val imgPiedrin: ImageView = findViewById(R.id.imgPiedrin)

        // Asignar acciones a los botones
        btnRock.setOnClickListener { playGame("Piedra", tvResult, tvPlayerScore, tvComputerScore, tvComputerChoice) }
        btnPaper.setOnClickListener { playGame("Papel", tvResult, tvPlayerScore, tvComputerScore, tvComputerChoice) }
        btnScissors.setOnClickListener { playGame("Tijera", tvResult, tvPlayerScore, tvComputerScore, tvComputerChoice) }
    }

    private fun playGame(
        playerChoice: String,
        tvResult: TextView,
        tvPlayerScore: TextView,
        tvComputerScore: TextView,
        tvComputerChoice: TextView
    ) {
        // Selección del jugador y de la computadora
        player.choice = playerChoice
        computer.choice = gameLogic.getComputerChoice()

        // Determinar el resultado
        val result = gameLogic.determineWinner(player.choice!!, computer.choice!!)
        tvResult.text = "Resultado: $result"
        tvComputerChoice.text = "Computadora eligió: ${computer.choice}"

        // Actualizar puntajes
        when (result) {
            "Ganaste" -> player.incrementScore()
            "Perdiste" -> computer.incrementScore()
        }
        tvPlayerScore.text = "Jugador: ${player.score}"
        tvComputerScore.text = "Computadora: ${computer.score}"
    }
}
