package com.example.piedra_papel_otijera.models

class GameLogic {
    private val choices = listOf("Piedra", "Papel", "Tijera")

    fun getComputerChoice(): String {
        return choices.random()
    }

    fun determineWinner(playerChoice: String, computerChoice: String): String {
        return when {
            playerChoice == computerChoice -> "Empate"
            playerChoice == "Piedra" && computerChoice == "Tijera" -> "Ganaste"
            playerChoice == "Tijera" && computerChoice == "Papel" -> "Ganaste"
            playerChoice == "Papel" && computerChoice == "Piedra" -> "Ganaste"
            else -> "Perdiste"
        }
    }
}
