package com.example.proyecto911

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TransitoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transitomenuactivity)

        val btnAtropello = findViewById<Button>(R.id.btnAtropello)
        val btnChoque = findViewById<Button>(R.id.btnChoque)
        val btnConducteEbrio = findViewById<Button>(R.id.btnConducteEbrio)



        btnAtropello.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }
        btnChoque.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }
        btnConducteEbrio.setOnClickListener { mostrarMensaje("Su Ayuda va en camino mantenga la calma") }


    }

    private fun mostrarMensaje(tipo: String) {
        Toast.makeText(this, "$tipo Gracias", Toast.LENGTH_SHORT).show()
    }
}