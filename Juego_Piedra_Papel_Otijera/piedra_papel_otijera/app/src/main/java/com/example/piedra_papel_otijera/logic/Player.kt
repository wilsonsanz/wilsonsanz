package com.example.piedra_papel_otijera.logic

class Player(val name: String) {
    var score: Int = 0
    var choice: String? = null

    fun incrementScore() {
        score++
    }
}
