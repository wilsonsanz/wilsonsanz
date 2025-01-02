package com.example.proyecto911

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BomberoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bomberomenuactivity)

        val btnVehiculo_Llamas = findViewById<Button>(R.id.btnVehiculo_Llamas)
        val btnCasallamas = findViewById<Button>(R.id.btnCasallamas)
        val btnRescate = findViewById<Button>(R.id.btnRescate)



        btnVehiculo_Llamas.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }
        btnCasallamas.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }
        btnRescate.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }


    }

    private fun mostrarMensaje(tipo: String) {
        Toast.makeText(this, "$tipo Gracias", Toast.LENGTH_SHORT).show()
    }
}